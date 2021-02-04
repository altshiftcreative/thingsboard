package com.asc.bluewaves.lwm2m.service;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.in;
import static org.eclipse.leshan.client.object.Security.psk;
import static org.eclipse.leshan.core.LwM2mId.DEVICE;
import static org.eclipse.leshan.core.LwM2mId.LOCATION;
import static org.eclipse.leshan.core.LwM2mId.SECURITY;
import static org.eclipse.leshan.core.LwM2mId.SERVER;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.conversions.Bson;
import org.eclipse.leshan.client.californium.LeshanClient;
import org.eclipse.leshan.client.californium.LeshanClientBuilder;
import org.eclipse.leshan.client.object.Server;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.core.model.InvalidDDFFileException;
import org.eclipse.leshan.core.model.InvalidModelException;
import org.eclipse.leshan.core.model.LwM2mModel;
import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.model.StaticModel;
import org.eclipse.leshan.core.request.BindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.asc.bluewaves.lwm2m.exception.errors.BadRequestException;
import com.asc.bluewaves.lwm2m.exception.errors.ResourceAlreadyExistException;
import com.asc.bluewaves.lwm2m.model.domain.Client;
import com.asc.bluewaves.lwm2m.model.dto.ClientDTO;
import com.asc.bluewaves.lwm2m.repository.Lwm2mClientRepository;
import com.asc.bluewaves.lwm2m.service.demo.AccelerometerSensor;
import com.asc.bluewaves.lwm2m.service.demo.MyDevice;
import com.asc.bluewaves.lwm2m.service.demo.MyLocation;
import com.asc.bluewaves.lwm2m.service.demo.RandomTemperatureSensor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service("ClientMongodbService")
@Slf4j
public class ClientMongodbService extends BaseService {

	public final static String[] modelPaths = new String[] { "3303.xml", "3313.xml" };

	@Value("${coap.secure-address}")
	private String secureAddress;

	@Value("${coap.secure-port}")
	private Integer securePort;
	
	@Value("${tb.base-url}")
	String tbBaseUrl;

	@Value("${tb.provision.key}")
	private String tbProvisionKey;

	@Value("${tb.provision.secret}")
	private String tbProvisionSecret;

	// MongoDB template
	private final MongoTemplate mongoTemplate;
	private Lwm2mClientRepository lwm2mClientRepository;

	ClientMongodbService(MongoTemplate mongoTemplate, Lwm2mClientRepository lwm2mClientRepository) {
		this.mongoTemplate = mongoTemplate;
		this.lwm2mClientRepository = lwm2mClientRepository;
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
		if (!StringUtils.hasText(endpoint)) {
			throw new BadRequestException("Endpoint list cannot be empty");
		}
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
		String address = isSecure ? "coaps://" : "coap://";
		address += secureAddress + ":" + securePort;
		return address;
	}

	String getNewAccessToken(String endpoint) {
		String accessTokenUrl = tbBaseUrl + "/v1/provision";

		RestTemplate restTemplate = new RestTemplate();
		final ObjectMapper mapper = new ObjectMapper();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, String> body = new HashMap<>();

		try {
			headers.set("Authorization", "Bearer " + getCurrentToken());

			body.put("deviceName", endpoint);
			body.put("provisionDeviceKey", tbProvisionKey);
			body.put("provisionDeviceSecret", tbProvisionSecret);

			HttpEntity<String> request = new HttpEntity<String>(mapper.writeValueAsString(body), headers);
			ResponseEntity<String> response = restTemplate.postForEntity(accessTokenUrl, request, String.class);

			JsonNode root = mapper.readTree(response.getBody());

			if (root.findValue("credentialsValue") == null) {
				return "";
			}
			return root.findValue("credentialsValue").asText();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	// Just use for demo, but no need in prod since the device will connect in its own
	public void addClientToServer(ClientDTO clientDto) {
		if (clientDto == null) {
			throw new BadRequestException("Endpoint is not exist");
		}
		log.info("Initialize client {} from server as demo", clientDto.getEndpoint());
		
		LeshanClientBuilder clientBuilder = new LeshanClientBuilder(clientDto.getEndpoint());

        // Initialize model
        List<ObjectModel> models = ObjectLoader.loadDefault();
        try {
			models.addAll(ObjectLoader.loadDdfResources("/models/", modelPaths));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidModelException e) {
			e.printStackTrace();
		} catch (InvalidDDFFileException e) {
			e.printStackTrace();
		}

        // Initialize object list
        final LwM2mModel model = new StaticModel(models);
        final ObjectsInitializer initializer = new ObjectsInitializer(model);

		initializer.setInstancesForObject(SECURITY,
				psk(getServerAddress(true), 123, clientDto.getIdentity().getBytes(), clientDto.getPreSharedKey().getBytes()));

		initializer.setInstancesForObject(SERVER, new Server(123, 300l, BindingMode.U, false));
		initializer.setInstancesForObject(DEVICE, new MyDevice());
		initializer.setInstancesForObject(LOCATION, new MyLocation());
		initializer.setInstancesForObject(3303, new RandomTemperatureSensor());
		initializer.setInstancesForObject(3313, new AccelerometerSensor());

		// add it to the client
		clientBuilder.setObjects(initializer.createAll());

		LeshanClient client = clientBuilder.build();
		client.start();
	}

	public void runClient(String endpoint) {
		if (!StringUtils.hasText(endpoint)) {
			throw new BadRequestException("Endpoint cannot be empty");
		}
		addClientToServer(getByEndpoint(endpoint));
	}
}
