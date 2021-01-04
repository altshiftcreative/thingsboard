package com.asc.bluewaves.lwm2m.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.leshan.core.model.LwM2mModel;
import org.eclipse.leshan.core.util.json.JsonException;
import org.eclipse.leshan.server.californium.LeshanServer;
import org.eclipse.leshan.server.registration.Registration;
import org.springframework.stereotype.Service;

import com.asc.bluewaves.lwm2m.model.ObjectModelSerDes;

@Service("objSpecsService")
public class ObjectSpecsService {

	private final LeshanServer server;
	private final ObjectModelSerDes serializer;

	ObjectSpecsService(LeshanServer server) {
		this.server = server;
		serializer = new ObjectModelSerDes();
	}

	public void getClientObjectSpec(HttpServletRequest req, HttpServletResponse resp, String clientEndpoint)
			throws ServletException, IOException {
		// Validate path : it must be /clientEndpoint
		if (clientEndpoint == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path");
			return;
		}

		// Get registration
		Registration registration = server.getRegistrationService().getByEndpoint(clientEndpoint);
		if (registration == null) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().format("no registered client with id '%s'", clientEndpoint).flush();
			return;
		}

		// Get Model for this registration
		try {
			LwM2mModel model = server.getModelProvider().getObjectModel(registration);
			resp.setContentType("application/json");
			resp.getOutputStream().write(serializer.bSerialize(model.getObjectModels()));
			resp.setStatus(HttpServletResponse.SC_OK);
		} catch (JsonException e) {
			throw new ServletException(e);
		}
		return;
	}
}
