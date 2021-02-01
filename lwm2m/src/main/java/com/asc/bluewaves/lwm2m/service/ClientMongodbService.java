package com.asc.bluewaves.lwm2m.service;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.in;
import static org.eclipse.leshan.client.object.Security.psk;
import static org.eclipse.leshan.core.LwM2mId.DEVICE;
import static org.eclipse.leshan.core.LwM2mId.LOCATION;
import static org.eclipse.leshan.core.LwM2mId.SECURITY;
import static org.eclipse.leshan.core.LwM2mId.SERVER;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.bson.conversions.Bson;
import org.eclipse.leshan.client.californium.LeshanClient;
import org.eclipse.leshan.client.californium.LeshanClientBuilder;
import org.eclipse.leshan.client.object.Server;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.core.request.BindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.asc.bluewaves.lwm2m.exception.errors.BadRequestException;
import com.asc.bluewaves.lwm2m.exception.errors.ResourceAlreadyExistException;
import com.asc.bluewaves.lwm2m.model.domain.Client;
import com.asc.bluewaves.lwm2m.model.dto.ClientDTO;
import com.asc.bluewaves.lwm2m.repository.Lwm2mClientRepository;
import com.asc.bluewaves.lwm2m.service.demo.MyDevice;
import com.asc.bluewaves.lwm2m.service.demo.MyLocation;

import lombok.extern.slf4j.Slf4j;

@Service("ClientMongodbService")
@Slf4j
public class ClientMongodbService extends BaseService {

	@Value("${coap.secure-address}")
	String secureAddress;

	@Value("${coap.secure-port}")
	Integer securePort;

	// MongoDB template
	private final MongoTemplate mongoTemplate;
	private Lwm2mClientRepository lwm2mClientRepository;

	ClientMongodbService(MongoTemplate mongoTemplate, Lwm2mClientRepository lwm2mClientRepository) {
		this.mongoTemplate = mongoTemplate;
		this.lwm2mClientRepository = lwm2mClientRepository;
	}

	@PostConstruct
	void init() {
		// Just use for demo, but no need in prod since the device will connect in its own
		loadClientFromDB();
	}

	// Database method
	// getByEndpoint from the Database
	public ClientDTO getByEndpoint(String endpoint) {
		Optional<Client> clientOpt = lwm2mClientRepository.findByEndpoint(endpoint);
		if (clientOpt.isPresent()) {
			return new ClientDTO(clientOpt.get());
		}
		return null;
	}

	// getByIdentity
	public ClientDTO getByIdentity(String identity) {
		Optional<Client> clientOpt = lwm2mClientRepository.findByIdentity(identity);
		if (clientOpt.isPresent()) {
			return new ClientDTO(clientOpt.get());
		}
		return null;
	}

	// getAllCurrentUserClients as a list from database
	public List<ClientDTO> getAllCurrentUserClients() {
		List<Client> clients = lwm2mClientRepository.findAll();
		return clients.stream().map(client -> new ClientDTO(client)).collect(Collectors.toList());
	}

	// getAllCurrentUserClients as a page from database
	public Page<ClientDTO> getAllCurrentUserClients(Pageable pageable) {
		return lwm2mClientRepository.findByOwner(getCurrentUsername(), pageable).map(ClientDTO::new);
	}

	// add client security info and data to the database
	public ClientDTO addClient(ClientDTO dto) {
		if (dto.getIdentity() != null) {
			Optional<Client> infoByIdentity = lwm2mClientRepository.findByIdentity(dto.getIdentity());
			if (infoByIdentity != null && infoByIdentity.isPresent() && !dto.getEndpoint().equals(infoByIdentity.get().getEndpoint())) {
				throw new ResourceAlreadyExistException("PSK Identity " + dto.getIdentity() + " is already used");
			}
			// Get access token for new device
			dto.setAccessToken(getNewAccessToken(dto.getEndpoint()));
			dto.setOwner(getCurrentUsername());

			Client client = lwm2mClientRepository.save(new Client(dto));
			return new ClientDTO(client);
		}
		return null;
	}

	// remove a client by endpoint and current user is the owner
	public void removeClient(String endpoint) {
		lwm2mClientRepository.deleteByEndpointAndOwner(endpoint, getCurrentUsername());
	}

	public void removeClient(List<String> endpoints) {
		if (endpoints == null || endpoints.isEmpty()) {
			throw new BadRequestException("Endpoint list cannot be empty");
		}
		
		Bson filter = and(in("endpoint", endpoints));
		mongoTemplate.getCollection("client").deleteMany(filter);
	}

	private String getServerAddress(boolean isSecure) {
		// InetSocketAddress socket = isSecure ? server.getSecuredAddress() : server.getUnsecuredAddress();
		String address = isSecure ? "coaps://" : "coap://";
		// address += socket.getHostName() + ":" + socket.getPort();
		address += secureAddress + ":" + securePort;
		return address;
	}

	// Just use for demo, but no need in prod since the device will connect in its own
	private void loadClientFromDB() {
		log.info("Trying to register clients saved in DB....");
		for (ClientDTO clientDto : getAllCurrentUserClients()) {
			addClientToServer(clientDto);
		}

//		MongoCursor<Document> clients = mongoTemplate.getCollection("client").find().iterator();
//		while (clients.hasNext()) {
//			ClientDTO clientDto = mongoTemplate.getConverter().read(ClientDTO.class, clients.next());
//			addClientToServer(clientDto);
//		}
	}

	// Just use for demo, but no need in prod since the device will connect in its own
	public void addClientToServer(ClientDTO clientDto) {
//		log.info("Start creating new LWM2M client...");
//		log.info("Initilize LWM2M client builder...");

		LeshanClientBuilder clientBuilder = new LeshanClientBuilder(clientDto.getEndpoint());

		// create objects
		ObjectsInitializer initializer = new ObjectsInitializer();

		// Any endpoint that have security method saved in store, it must use it or the Registration failed: FORBIDDEN.
		// Any new endpoint without security method try to register in the secure address will be rejected
		// Identity must not duplicated

		// SecurityInfo info = securityStore.getByEndpoint(clientDto.getEndpoint());
		// if (info != null && info.usePSK()) {
		initializer.setInstancesForObject(SECURITY,
				psk(getServerAddress(true), 123, clientDto.getIdentity().getBytes(), clientDto.getPreSharedKey().getBytes()));

		initializer.setInstancesForObject(SERVER, new Server(123, 300l, BindingMode.U, false));
		initializer.setInstancesForObject(DEVICE, new MyDevice());
		initializer.setInstancesForObject(LOCATION, new MyLocation());
		// initializer.setInstancesForObject(3303, new RandomTemperatureSensor());

		// add it to the client
		clientBuilder.setObjects(initializer.createAll());

		LeshanClient client = clientBuilder.build();
		client.start();
	}

	public void runClient(String endpoint) {
		addClientToServer(getByEndpoint(endpoint));
	}
}
