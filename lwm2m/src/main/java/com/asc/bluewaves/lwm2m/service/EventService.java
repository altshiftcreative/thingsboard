package com.asc.bluewaves.lwm2m.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.jetty.servlets.EventSource;
import org.eclipse.jetty.servlets.EventSourceServlet;
import org.eclipse.leshan.core.Link;
import org.eclipse.leshan.core.node.LwM2mNode;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.request.ContentFormat;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.server.californium.LeshanServer;
import org.eclipse.leshan.server.observation.ObservationListener;
import org.eclipse.leshan.server.queue.PresenceListener;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationListener;
import org.eclipse.leshan.server.registration.RegistrationUpdate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.asc.bluewaves.lwm2m.model.dto.ClientDTO;
import com.asc.bluewaves.lwm2m.model.json.LwM2mNodeSerializer;
import com.asc.bluewaves.lwm2m.model.json.RegistrationSerializer;
import com.asc.bluewaves.lwm2m.model.log.CoapMessage;
import com.asc.bluewaves.lwm2m.model.log.CoapMessageListener;
import com.asc.bluewaves.lwm2m.model.log.CoapMessageTracer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service("EventService")
@Slf4j
public class EventService extends EventSourceServlet {

	// @Value("${tb.base-url}")
	private String tbBaseUrl;

	private static final long serialVersionUID = 1L;

	private static final String EVENT_DEREGISTRATION = "DEREGISTRATION";
	private static final String EVENT_UPDATED = "UPDATED";
	private static final String EVENT_REGISTRATION = "REGISTRATION";

	private static final String EVENT_AWAKE = "AWAKE";
	private static final String EVENT_SLEEPING = "SLEEPING";

	private static final String EVENT_NOTIFICATION = "NOTIFICATION";
	private static final String EVENT_COAP_LOG = "COAPLOG";
	private static final String QUERY_PARAM_ENDPOINT = "ep";

	private final ClientMongodbService clientMongodbService;

	private final Gson gson;

	private final LeshanServer server;

	private final CoapMessageTracer coapMessageTracer;

	private Set<LeshanEventSource> eventSources = Collections.newSetFromMap(new ConcurrentHashMap<LeshanEventSource, Boolean>());

	private final RegistrationListener registrationListener = new RegistrationListener() {

		@Override
		public void registered(Registration registration, Registration previousReg, Collection<Observation> previousObsersations) {
			String jReg = gson.toJson(registration);
			sendTelemetryData(registration);
			sendEvent(EVENT_REGISTRATION, jReg, registration.getEndpoint());
		}

		@Override
		public void updated(RegistrationUpdate update, Registration updatedRegistration, Registration previousRegistration) {
			RegUpdate regUpdate = new RegUpdate();
			regUpdate.registration = updatedRegistration;
			regUpdate.update = update;
			String jReg = gson.toJson(regUpdate);
			sendEvent(EVENT_UPDATED, jReg, updatedRegistration.getEndpoint());
		}

		@Override
		public void unregistered(Registration registration, Collection<Observation> observations, boolean expired, Registration newReg) {
			String jReg = gson.toJson(registration);
			sendEvent(EVENT_DEREGISTRATION, jReg, registration.getEndpoint());
		}

	};

	public final PresenceListener presenceListener = new PresenceListener() {

		@Override
		public void onSleeping(Registration registration) {
			String data = new StringBuilder("{\"ep\":\"").append(registration.getEndpoint()).append("\"}").toString();

			sendEvent(EVENT_SLEEPING, data, registration.getEndpoint());
		}

		@Override
		public void onAwake(Registration registration) {
			String data = new StringBuilder("{\"ep\":\"").append(registration.getEndpoint()).append("\"}").toString();
			sendEvent(EVENT_AWAKE, data, registration.getEndpoint());
		}
	};

	private final ObservationListener observationListener = new ObservationListener() {

		@Override
		public void cancelled(Observation observation) {
		}

		@Override
		public void onResponse(Observation observation, Registration registration, ObserveResponse response) {
			if (log.isDebugEnabled()) {
				log.debug("Received notification from [{}] containing value [{}]", observation.getPath(), response.getContent().toString());
			}

			if (registration != null) {
				String data = new StringBuilder("{\"ep\":\"").append(registration.getEndpoint()).append("\",\"res\":\"")
						.append(observation.getPath().toString()).append("\",\"val\":").append(gson.toJson(response.getContent()))
						.append("}").toString();

				sendEvent(EVENT_NOTIFICATION, data, registration.getEndpoint());
			}
		}

		@Override
		public void onError(Observation observation, Registration registration, Exception error) {
			if (log.isWarnEnabled()) {
				log.warn(String.format("Unable to handle notification of [%s:%s]", observation.getRegistrationId(), observation.getPath()),
						error);
			}
		}

		@Override
		public void newObservation(Observation observation, Registration registration) {
		}
	};

	public EventService(ClientMongodbService clientMongodbService, LeshanServer server) {
		this.server = server;
		server.getRegistrationService().addListener(this.registrationListener);
		server.getObservationService().addListener(this.observationListener);
		server.getPresenceService().addListener(this.presenceListener);

		// add an intercepter to each endpoint to trace all CoAP messages
		coapMessageTracer = new CoapMessageTracer(server.getRegistrationService());
		for (Endpoint endpoint : server.coap().getServer().getEndpoints()) {
			endpoint.addInterceptor(coapMessageTracer);
		}

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeHierarchyAdapter(Registration.class, new RegistrationSerializer(server.getPresenceService()));
		gsonBuilder.registerTypeHierarchyAdapter(LwM2mNode.class, new LwM2mNodeSerializer());
		gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		this.gson = gsonBuilder.create();

		this.clientMongodbService = clientMongodbService;
	}

	private synchronized void sendEvent(String event, String data, String endpoint) {
		if (log.isDebugEnabled()) {
			log.debug("Dispatching {} event from endpoint {}", event, endpoint);
		}

		for (LeshanEventSource eventSource : eventSources) {
			if (eventSource.getEndpoint() == null || eventSource.getEndpoint().equals(endpoint)) {
				eventSource.sentEvent(event, data);
			}
		}
	}

	class ClientCoapListener implements CoapMessageListener {

		private final String endpoint;

		ClientCoapListener(String endpoint) {
			this.endpoint = endpoint;
		}

		@Override
		public void trace(CoapMessage message) {
			JsonElement coapLog = EventService.this.gson.toJsonTree(message);
			coapLog.getAsJsonObject().addProperty("ep", this.endpoint);
			String coapLogWithEndPoint = EventService.this.gson.toJson(coapLog);
			sendEvent(EVENT_COAP_LOG, coapLogWithEndPoint, endpoint);
		}

	}

	private void cleanCoapListener(String endpoint) {
		// remove the listener if there is no more eventSources for this endpoint
		for (LeshanEventSource eventSource : eventSources) {
			if (eventSource.getEndpoint() == null || eventSource.getEndpoint().equals(endpoint)) {
				return;
			}
		}
		coapMessageTracer.removeListener(endpoint);
	}

	@Override
	protected EventSource newEventSource(HttpServletRequest req) {
		String endpoint = req.getParameter(QUERY_PARAM_ENDPOINT);
		return new LeshanEventSource(endpoint);
	}

	private class LeshanEventSource implements EventSource {

		private String endpoint;
		private Emitter emitter;

		public LeshanEventSource(String endpoint) {
			this.endpoint = endpoint;
		}

		@Override
		public void onOpen(Emitter emitter) throws IOException {
			this.emitter = emitter;
			eventSources.add(this);
			if (endpoint != null) {
				coapMessageTracer.addListener(endpoint, new ClientCoapListener(endpoint));
			}
		}

		@Override
		public void onClose() {
			cleanCoapListener(endpoint);
			eventSources.remove(this);
		}

		public void sentEvent(String event, String data) {
			try {
				emitter.event(event, data);
			} catch (IOException e) {
				e.printStackTrace();
				onClose();
			}
		}

		public String getEndpoint() {
			return endpoint;
		}
	}

	@SuppressWarnings("unused")
	private class RegUpdate {
		public Registration registration;
		public RegistrationUpdate update;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			super.doGet(request, response);
		} catch (IOException | ServletException e) {
			throw new RuntimeException(e);
		}
	}

	private void sendTelemetryData(Registration registration) {
		boolean flag = false;
		for (Link link : registration.getObjectLinks()) {
			if ("/3313/0".equals(link.getUrl())) {
				flag = true;
				break;
			}
		}
		if (!flag) {
			return;
		}

		ClientDTO client = clientMongodbService.getByEndpoint(registration.getEndpoint());
		if (client == null || !StringUtils.hasText(client.getAccessToken())) {
			return;
		}
		String telemetryUrl = clientMongodbService.tbBaseUrl + "/v1/" + client.getAccessToken() + "/telemetry";

		RestTemplate restTemplate = new RestTemplate();
		final ObjectMapper mapper = new ObjectMapper();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> body = getData(registration, mapper);

		try {
			HttpEntity<String> teleRequest = new HttpEntity<String>(mapper.writeValueAsString(body), headers);
			restTemplate.postForEntity(telemetryUrl, teleRequest, String.class);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Map<String, Object> getData(Registration registration, ObjectMapper mapper) {
		try {
			ReadRequest request = new ReadRequest(ContentFormat.fromName("TLV"), "/3313/0");
			ReadResponse cResponse = server.send(registration, request, 5000);
			String dataStr = this.gson.toJson(cResponse);

			JsonNode resources = mapper.readTree(dataStr).findValue("resources");
			ObjectReader reader = mapper.readerFor(new TypeReference<List<Resource>>() { });
			List<Resource> list = reader.readValue(resources);
			
			Map<String, Object> data = new HashMap<>();
			for (Resource resource : list) {
				if (resource.id == 5702) {
					data.put("xValue", resource.value);
					
				} else if (resource.id == 5703) {
					data.put("yValue", resource.value);
					
				} else if (resource.id == 5704) {
					data.put("zValue", resource.value);
				}
			}
			return data;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static class Resource {
		private int id;
		private Object value;

		Resource() {
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}
	}
}