package com.asc.bluewaves.lwm2m.service;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static org.eclipse.leshan.client.object.Security.psk;
import static org.eclipse.leshan.core.LwM2mId.DEVICE;
import static org.eclipse.leshan.core.LwM2mId.LOCATION;
import static org.eclipse.leshan.core.LwM2mId.SECURITY;
import static org.eclipse.leshan.core.LwM2mId.SERVER;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.leshan.client.californium.LeshanClient;
import org.eclipse.leshan.client.californium.LeshanClientBuilder;
import org.eclipse.leshan.client.object.Server;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.core.attributes.AttributeSet;
import org.eclipse.leshan.core.model.LwM2mModel;
import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ResourceModel;
import org.eclipse.leshan.core.model.ResourceModel.Type;
import org.eclipse.leshan.core.model.StaticModel;
import org.eclipse.leshan.core.node.LwM2mNode;
import org.eclipse.leshan.core.node.LwM2mObjectInstance;
import org.eclipse.leshan.core.node.LwM2mPath;
import org.eclipse.leshan.core.node.LwM2mSingleResource;
import org.eclipse.leshan.core.node.codec.CodecException;
import org.eclipse.leshan.core.request.BindingMode;
import org.eclipse.leshan.core.request.ContentFormat;
import org.eclipse.leshan.core.request.CreateRequest;
import org.eclipse.leshan.core.request.DeleteRequest;
import org.eclipse.leshan.core.request.DiscoverRequest;
import org.eclipse.leshan.core.request.ExecuteRequest;
import org.eclipse.leshan.core.request.ObserveRequest;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.request.WriteAttributesRequest;
import org.eclipse.leshan.core.request.WriteRequest;
import org.eclipse.leshan.core.request.WriteRequest.Mode;
import org.eclipse.leshan.core.request.exception.ClientSleepingException;
import org.eclipse.leshan.core.request.exception.InvalidRequestException;
import org.eclipse.leshan.core.request.exception.InvalidResponseException;
import org.eclipse.leshan.core.request.exception.RequestCanceledException;
import org.eclipse.leshan.core.request.exception.RequestRejectedException;
import org.eclipse.leshan.core.response.CreateResponse;
import org.eclipse.leshan.core.response.DeleteResponse;
import org.eclipse.leshan.core.response.DiscoverResponse;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.LwM2mResponse;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteAttributesResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.eclipse.leshan.server.californium.LeshanServer;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.security.EditableSecurityStore;
import org.eclipse.leshan.server.security.SecurityInfo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.asc.bluewaves.lwm2m.converter.MagicLwM2mValueConverter;
import com.asc.bluewaves.lwm2m.errors.ResourceAlreadyExistException;
import com.asc.bluewaves.lwm2m.errors.ResourceDoesNotExistException;
import com.asc.bluewaves.lwm2m.model.ClientDTO;
import com.asc.bluewaves.lwm2m.model.json.LwM2mNodeDeserializer;
import com.asc.bluewaves.lwm2m.model.json.LwM2mNodeSerializer;
import com.asc.bluewaves.lwm2m.model.json.RegistrationSerializer;
import com.asc.bluewaves.lwm2m.model.json.ResponseSerializer;
import com.asc.bluewaves.lwm2m.service.demo.MyDevice;
import com.asc.bluewaves.lwm2m.service.demo.MyLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mongodb.client.MongoCursor;

import lombok.extern.slf4j.Slf4j;

@Service("ClientService")
@Slf4j
public class ClientService {

	// MongoDB template
	private final MongoTemplate mongoTemplate;

	// Server fields
	private final LeshanServer server;
	private final EditableSecurityStore securityStore;
	private final LwM2mModel model;
	private final MagicLwM2mValueConverter converter;

	// Mapper
	private final ObjectMapper mapper;
	private final Gson gson;

	ClientService(MongoTemplate mongoTemplate, LeshanServer server, EditableSecurityStore securityStore)
			throws JsonProcessingException, InterruptedException {
		this.mongoTemplate = mongoTemplate;

		this.server = server;
		this.securityStore = securityStore;
		this.model = new StaticModel(ObjectLoader.loadDefault());
		this.converter = new MagicLwM2mValueConverter();

		// Initialize JSON mapper
		this.mapper = new ObjectMapper();
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeHierarchyAdapter(Registration.class, new RegistrationSerializer(server.getPresenceService()));
		gsonBuilder.registerTypeHierarchyAdapter(LwM2mResponse.class, new ResponseSerializer());
		gsonBuilder.registerTypeHierarchyAdapter(LwM2mNode.class, new LwM2mNodeSerializer());
		gsonBuilder.registerTypeHierarchyAdapter(LwM2mNode.class, new LwM2mNodeDeserializer());
		gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		this.gson = gsonBuilder.create();

		// Just use for demo, but no need in prod since the device will connect in its own
		loadClientFromDB();
	}

	private String getServerAddress(boolean isSecure) {
		InetSocketAddress socket = isSecure ? server.getSecuredAddress() : server.getUnsecuredAddress();
		String address = isSecure ? "coaps://" : "coap://";
		address += socket.getHostName() + ":" + socket.getPort();
		return address;
	}

	// Just use for demo, but no need in prod since the device will connect in its own
	private void loadClientFromDB() throws InterruptedException, JsonProcessingException {
		log.info("Trying to register clients saved in DB....");
		MongoCursor<Document> clients = mongoTemplate.getCollection("client").find().iterator();
		while (clients.hasNext()) {
			ClientDTO clientDto = mongoTemplate.getConverter().read(ClientDTO.class, clients.next());
			addClientToServer(clientDto);
		}
	}

	// Just use for demo, but no need in prod since the device will connect in its own
	public void addClientToServer(ClientDTO clientDto) throws JsonProcessingException {
		log.info("Start creating new LWM2M client...");
		log.info("Initilize LWM2M client builder...");

		LeshanClientBuilder clientBuilder = new LeshanClientBuilder(clientDto.getEndpoint());

		// create objects
		ObjectsInitializer initializer = new ObjectsInitializer();

		// Any endpoint that have security method saved in store, it must use it or the Registration failed: FORBIDDEN.
		// Any new endpoint without security method try to register in the secure address will be rejected
		// Identity must not duplicated

		SecurityInfo info = securityStore.getByEndpoint(clientDto.getEndpoint());
		if (info != null && info.usePSK()) {
			initializer.setInstancesForObject(SECURITY,
					psk(getServerAddress(true), 123, info.getIdentity().getBytes(), info.getPreSharedKey()));

			// We have to add the code for security using x502 certificate and RPK
		} else {
			return;
			// No insceure device can connect
			// initializer.setInstancesForObject(SECURITY, Security.noSec(getServerAddress(false), 12345));
		}

		initializer.setInstancesForObject(SERVER, new Server(123, 300l, BindingMode.U, false));
		initializer.setInstancesForObject(DEVICE, new MyDevice());
		initializer.setInstancesForObject(LOCATION, new MyLocation());
		// initializer.setInstancesForObject(3303, new RandomTemperatureSensor());

		// add it to the client
		clientBuilder.setObjects(initializer.createAll());

		LeshanClient client = clientBuilder.build();
		client.start();
	}

	public void saveClient(ClientDTO clientDto) throws ResourceAlreadyExistException, IOException {
		// check if the endpoint client is exist
		Bson filter = and(eq("endpoint", clientDto.getEndpoint()));
		Iterable<Document> clientDB = mongoTemplate.getCollection("client").find(filter);
		if (clientDB.iterator().hasNext()) {
			throw new ResourceAlreadyExistException("The client you are trying to create is already exist: " + clientDto.getEndpoint());
		}

		// Add the client to our running Leshan server
		addClientToServer(clientDto);
		clientDto.setAccessToken(getNewAccessToken(clientDto.getEndpoint()));

		// Save client to MongoDB
		mongoTemplate.getCollection("client").insertOne(Document.parse(mapper.writeValueAsString(clientDto)));
	}

	private String getNewAccessToken(String endpoint) throws IOException {
		String loginUrl = "http://localhost:8080/api/auth/login";
		String accessTokenUrl = "http://localhost:8080/api/v1/provision";
		String username = "hassan@altshiftcreative.com";
		String password = "Hasan1998!";
		String lwm2mKey = "r3rd5n77w1bpht21kp91";
		String lwm2mSecret = "i9x798wdnbzirnax7b43";

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// First request, get token
		Map<String, String> body = new HashMap<>();
		body.put("username", username);
		body.put("password", password);

		HttpEntity<String> request = new HttpEntity<String>(mapper.writeValueAsString(body), headers);
		ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, request, String.class);

		JsonNode root = mapper.readTree(response.getBody());
		String token = root.findValue("token").asText("");

		// Second request, get access token
		headers.set("Authorization", "Bearer " + token);

		body.clear();
		body.put("deviceName", endpoint);
		body.put("provisionDeviceKey", lwm2mKey);
		body.put("provisionDeviceSecret", lwm2mSecret);

		request = new HttpEntity<String>(mapper.writeValueAsString(body), headers);
		response = restTemplate.postForEntity(accessTokenUrl, request, String.class);

		root = mapper.readTree(response.getBody());

		if (root.findValue("credentialsValue") == null) {
			return "";
		}
		return root.findValue("credentialsValue").asText();
	}

	public void deleteClient(List<String> endpoints) {
		if (endpoints == null || endpoints.isEmpty()) {
			return;
		}
		Bson filter = and(in("endpoint", endpoints));
		mongoTemplate.getCollection("client").deleteMany(filter);

		for (String endpoint : endpoints) {
			securityStore.remove(endpoint, true);
		}
	}

	public void doReadRequest(HttpServletRequest req, HttpServletResponse resp, String format, Integer timeout)
			throws ResourceDoesNotExistException, Exception {

		// all registered clients
		// GET /api/clients or /api/clients/
		if (req.getRequestURI().endsWith("clients") || req.getRequestURI().endsWith("clients/")) {
			Collection<Registration> registrations = new ArrayList<>();
			for (Iterator<Registration> iterator = server.getRegistrationService().getAllRegistrations(); iterator.hasNext();) {
				registrations.add(iterator.next());
			}

			String json = this.gson.toJson(registrations.toArray(new Registration[] {}));
			resp.setContentType("application/json");
			resp.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
			resp.setStatus(HttpServletResponse.SC_OK);
			return;
		}

		String targetPath = req.getRequestURI().split("clients")[1];
		String[] path = StringUtils.split(targetPath, '/');
		if (path.length < 1) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path");
			return;
		}
		String clientEndpoint = path[0];

		// /endPoint : get client
		// GET /api/clients/ebraheem-fedora
		if (path.length == 1) {
			Registration registration = server.getRegistrationService().getByEndpoint(clientEndpoint);
			if (registration != null) {
				resp.setContentType("application/json");
				resp.getOutputStream().write(this.gson.toJson(registration).getBytes(StandardCharsets.UTF_8));
				resp.setStatus(HttpServletResponse.SC_OK);
			} else {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.getWriter().format("no registered client with id '%s'", clientEndpoint).flush();
			}
			return;
		}

		// /clients/endPoint/LWRequest/discover : do LightWeight M2M discover request on a given client.
		if (path.length >= 3 && "discover".equals(path[path.length - 1])) {
			String target = StringUtils.substringBetween(targetPath, clientEndpoint, "/discover");
			try {
				Registration registration = server.getRegistrationService().getByEndpoint(clientEndpoint);
				if (registration != null) {
					// create & process request
					DiscoverRequest request = new DiscoverRequest(target);
					DiscoverResponse cResponse = server.send(registration, request, timeout * 1000);
					processDeviceResponse(resp, cResponse);
				} else {
					resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					resp.getWriter().format("No registered client with id '%s'", clientEndpoint).flush();
				}
			} catch (RuntimeException | InterruptedException e) {
				handleException(e, resp);
			}
			return;
		}

		// /clients/endPoint/LWRequest : do LightWeight M2M read request on a given client.
		// GET /api/clients/ebraheem-fedora/1/0?format=TLV&timeout=5
		try {
			String target = StringUtils.removeStart(targetPath, "/" + clientEndpoint);
			Registration registration = server.getRegistrationService().getByEndpoint(clientEndpoint);
			if (registration != null) {
				// get content format
				ContentFormat contentFormat = format != null ? ContentFormat.fromName(format.toUpperCase()) : null;

				// create & process request
				ReadRequest request = new ReadRequest(contentFormat, target);
				ReadResponse cResponse = server.send(registration, request, timeout * 1000);
				processDeviceResponse(resp, cResponse);
			} else {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.getWriter().format("No registered client with id '%s'", clientEndpoint).flush();
			}
		} catch (RuntimeException | InterruptedException e) {
			handleException(e, resp);
		}
	}

	public void doUpdateRequest(HttpServletRequest req, HttpServletResponse res, Boolean replace, String format, Integer timeout)
			throws ResourceDoesNotExistException, Exception {
		String targetPath = req.getRequestURI().split("clients")[1];
		String[] path = StringUtils.split(targetPath, '/');
		String clientEndpoint = path[0];

		try {
			// at least /endpoint/objectId/instanceId
			// PUT /api/clients/ebraheem-fedora/1/0/7?format=TLV&timeout=5
			if (path.length < 3) {
				res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path");
				return;
			}

			// Update single object
			String target = StringUtils.removeStart(targetPath, "/" + clientEndpoint);
			Registration registration = server.getRegistrationService().getByEndpoint(clientEndpoint);
			if (registration != null) {
				if (path.length >= 3 && "attributes".equals(path[path.length - 1])) {
					// create & process request WriteAttributes request
					target = StringUtils.removeEnd(target, path[path.length - 1]);
					AttributeSet attributes = AttributeSet.parse(req.getQueryString());
					WriteAttributesRequest request = new WriteAttributesRequest(target, attributes);
					WriteAttributesResponse cResponse = server.send(registration, request, timeout * 1000);
					processDeviceResponse(res, cResponse);
				} else {

					ContentFormat contentFormat = format != null ? ContentFormat.fromName(format) : null;
					// create & process request
					LwM2mNode node = extractLwM2mNode(target, req, new LwM2mPath(target));
					WriteRequest request = new WriteRequest(replace ? Mode.REPLACE : Mode.UPDATE, contentFormat, target, node);
					WriteResponse cResponse = server.send(registration, request, timeout * 1000);
					processDeviceResponse(res, cResponse);
				}
			} else {
				res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				res.getWriter().format("No registered client with id '%s'", clientEndpoint).flush();
			}
		} catch (RuntimeException | InterruptedException e) {
			handleException(e, res);
		}
	}

	public void doPostRequest(HttpServletRequest req, HttpServletResponse resp, String format, Integer timeout)
			throws ServletException, IOException {

		String targetPath = req.getRequestURI().split("clients")[1];
		String[] path = StringUtils.split(targetPath, '/');
		String clientEndpoint = path[0];

		ContentFormat contentFormat = format != null ? ContentFormat.fromName(format) : null;

		// /clients/endPoint/LWRequest/observe : do LightWeight M2M observe request on a given client.
		// POST /api/clients/ebraheem-fedora/1/0/0/observe?format=TLV&timeout=5
		if (path.length >= 3 && "observe".equals(path[path.length - 1])) {
			try {
				String target = StringUtils.substringBetween(targetPath, clientEndpoint, "/observe");
				Registration registration = server.getRegistrationService().getByEndpoint(clientEndpoint);
				if (registration != null) {
					// create & process request
					ObserveRequest request = new ObserveRequest(contentFormat, target);
					ObserveResponse cResponse = server.send(registration, request, timeout * 1000);
					processDeviceResponse(resp, cResponse);
				} else {
					resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					resp.getWriter().format("no registered client with id '%s'", clientEndpoint).flush();
				}
			} catch (RuntimeException | InterruptedException e) {
				handleException(e, resp);
			}
			return;
		}

		String target = StringUtils.removeStart(targetPath, "/" + clientEndpoint);

		// /clients/endPoint/LWRequest : do LightWeight M2M execute request on a given client.
		if (path.length == 4) {
			try {
				Registration registration = server.getRegistrationService().getByEndpoint(clientEndpoint);
				if (registration != null) {
					String params = null;
					if (req.getContentLength() > 0) {
						params = IOUtils.toString(req.getInputStream(), StandardCharsets.UTF_8);
					}
					ExecuteRequest request = new ExecuteRequest(target, params);
					ExecuteResponse cResponse = server.send(registration, request, timeout * 1000);
					processDeviceResponse(resp, cResponse);
				} else {
					resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					resp.getWriter().format("no registered client with id '%s'", clientEndpoint).flush();
				}
			} catch (RuntimeException | InterruptedException e) {
				handleException(e, resp);
			}
			return;
		}

		// /clients/endPoint/LWRequest : do LightWeight M2M create request on a given client.
		if (2 <= path.length && path.length <= 3) {
			try {
				Registration registration = server.getRegistrationService().getByEndpoint(clientEndpoint);
				if (registration != null) {
					// create & process request
					LwM2mNode node = extractLwM2mNode(target, req, new LwM2mPath(target));
					if (node instanceof LwM2mObjectInstance) {
						CreateRequest request;
						if (node.getId() == LwM2mObjectInstance.UNDEFINED) {
							request = new CreateRequest(contentFormat, target, ((LwM2mObjectInstance) node).getResources().values());
						} else {
							request = new CreateRequest(contentFormat, target, (LwM2mObjectInstance) node);
						}

						CreateResponse cResponse = server.send(registration, request, timeout * 1000);
						processDeviceResponse(resp, cResponse);
					} else {
						throw new IllegalArgumentException("payload must contain an object instance");
					}
				} else {
					resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					resp.getWriter().format("no registered client with id '%s'", clientEndpoint).flush();
				}
			} catch (RuntimeException | InterruptedException e) {
				handleException(e, resp);
			}
			return;
		}
	}

	public void doDeleteRequest(HttpServletRequest req, HttpServletResponse resp, Integer timeout) throws ServletException, IOException {
		String targetPath = req.getRequestURI().split("clients")[1];
		String[] path = StringUtils.split(targetPath, '/');
		String clientEndpoint = path[0];

		// /clients/endPoint/LWRequest/observe : cancel observation for the given resource.
		if (path.length >= 3 && "observe".equals(path[path.length - 1])) {
			try {
				String target = StringUtils.substringsBetween(targetPath, clientEndpoint, "/observe")[0];
				Registration registration = server.getRegistrationService().getByEndpoint(clientEndpoint);
				if (registration != null) {
					server.getObservationService().cancelObservations(registration, target);
					resp.setStatus(HttpServletResponse.SC_OK);
				} else {
					resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					resp.getWriter().format("no registered client with id '%s'", clientEndpoint).flush();
				}
			} catch (RuntimeException e) {
				handleException(e, resp);
			}
			return;
		}

		// /clients/endPoint/LWRequest/ : delete instance
		try {
			String target = StringUtils.removeStart(targetPath, "/" + clientEndpoint);
			Registration registration = server.getRegistrationService().getByEndpoint(clientEndpoint);
			if (registration != null) {
				DeleteRequest request = new DeleteRequest(target);
				DeleteResponse cResponse = server.send(registration, request, timeout * 1000);
				processDeviceResponse(resp, cResponse);
			} else {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.getWriter().format("no registered client with id '%s'", clientEndpoint).flush();
			}
		} catch (RuntimeException | InterruptedException e) {
			handleException(e, resp);
		}
	}

	private void processDeviceResponse(HttpServletResponse resp, LwM2mResponse cResponse) throws IOException {
		if (cResponse == null) {
			resp.setStatus(HttpServletResponse.SC_GATEWAY_TIMEOUT);
			resp.getWriter().append("Request timeout").flush();
		} else {
			String response = this.gson.toJson(cResponse);
			resp.setContentType("application/json");
			resp.getOutputStream().write(response.getBytes());
			resp.setStatus(HttpServletResponse.SC_OK);
		}
	}

	private LwM2mNode extractLwM2mNode(String target, HttpServletRequest req, LwM2mPath path) throws IOException {
		String contentType = StringUtils.substringBefore(req.getContentType(), ";");
		if ("application/json".equals(contentType)) {
			String content = IOUtils.toString(req.getInputStream(), req.getCharacterEncoding());
			LwM2mNode node;
			try {
				node = gson.fromJson(content, LwM2mNode.class);
				if (node instanceof LwM2mSingleResource) {
					// TODO HACK resource type should be extracted from json value but this is not yet available.
					LwM2mSingleResource singleResource = (LwM2mSingleResource) node;
					ResourceModel resourceModel = model.getResourceModel(path.getObjectId(), singleResource.getId());
					if (resourceModel != null) {
						Type expectedType = resourceModel.type;
						Object expectedValue = converter.convertValue(singleResource.getValue(), singleResource.getType(), expectedType,
								path);
						node = LwM2mSingleResource.newResource(node.getId(), expectedValue, expectedType);
					}
				}
			} catch (JsonSyntaxException e) {
				throw new InvalidRequestException(e, "unable to parse json to tlv:%s", e.getMessage());
			}
			return node;
		} else if ("text/plain".equals(contentType)) {
			String content = IOUtils.toString(req.getInputStream(), req.getCharacterEncoding());
			int rscId = Integer.valueOf(target.substring(target.lastIndexOf("/") + 1));
			return LwM2mSingleResource.newStringResource(rscId, content);
		}
		throw new InvalidRequestException("content type %s not supported", req.getContentType());
	}

	private void handleException(Exception e, HttpServletResponse resp) throws IOException {
		if (e instanceof InvalidRequestException || e instanceof CodecException || e instanceof ClientSleepingException) {
			log.warn("Invalid request", e);
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().append("Invalid request:").append(e.getMessage()).flush();

		} else if (e instanceof RequestRejectedException) {
			log.warn("Request rejected", e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().append("Request rejected:").append(e.getMessage()).flush();

		} else if (e instanceof RequestCanceledException) {
			log.warn("Request cancelled", e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().append("Request cancelled:").append(e.getMessage()).flush();

		} else if (e instanceof InvalidResponseException) {
			log.warn("Invalid response", e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().append("Invalid Response:").append(e.getMessage()).flush();

		} else if (e instanceof InterruptedException) {
			log.warn("Thread Interrupted", e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().append("Thread Interrupted:").append(e.getMessage()).flush();

		} else {
			log.warn("Unexpected exception", e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().append("Unexpected exception:").append(e.getMessage()).flush();
		}
	}
}
