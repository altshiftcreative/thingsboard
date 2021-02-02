package com.asc.bluewaves.lwm2m.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.jetty.servlets.EventSource;
import org.eclipse.jetty.servlets.EventSourceServlet;
import org.eclipse.leshan.core.node.LwM2mNode;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.server.californium.LeshanServer;
import org.eclipse.leshan.server.observation.ObservationListener;
import org.eclipse.leshan.server.queue.PresenceListener;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationListener;
import org.eclipse.leshan.server.registration.RegistrationUpdate;
import org.springframework.stereotype.Service;

import com.asc.bluewaves.lwm2m.model.json.LwM2mNodeSerializer;
import com.asc.bluewaves.lwm2m.model.json.RegistrationSerializer;
import com.asc.bluewaves.lwm2m.model.log.CoapMessage;
import com.asc.bluewaves.lwm2m.model.log.CoapMessageListener;
import com.asc.bluewaves.lwm2m.model.log.CoapMessageTracer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import lombok.extern.slf4j.Slf4j;

@Service("EventService")
@Slf4j
public class EventService extends EventSourceServlet {

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

	// private final LeshanServer server;

	private final CoapMessageTracer coapMessageTracer;

	private Set<LeshanEventSource> eventSources = Collections.newSetFromMap(new ConcurrentHashMap<LeshanEventSource, Boolean>());

	private final RegistrationListener registrationListener = new RegistrationListener() {

		@Override
		public void registered(Registration registration, Registration previousReg, Collection<Observation> previousObsersations) {
			// TODO: if client is not in our DB destroy it

			String jReg = gson.toJson(registration);
			log.info("Client regestiration: " + registration.getEndpoint());
			sendEvent(EVENT_REGISTRATION, jReg, registration.getEndpoint());
		}

		@Override
		public void updated(RegistrationUpdate update, Registration updatedRegistration, Registration previousRegistration) {
			RegUpdate regUpdate = new RegUpdate();
			regUpdate.registration = updatedRegistration;
			regUpdate.update = update;
			String jReg = gson.toJson(regUpdate);
			log.info("Client update regestiration: " + updatedRegistration.getEndpoint());
			sendEvent(EVENT_UPDATED, jReg, updatedRegistration.getEndpoint());
		}

		@Override
		public void unregistered(Registration registration, Collection<Observation> observations, boolean expired, Registration newReg) {
			String jReg = gson.toJson(registration);
			log.info("Client deregestiration: " + registration.getEndpoint());
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
		// this.server = server;
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
}