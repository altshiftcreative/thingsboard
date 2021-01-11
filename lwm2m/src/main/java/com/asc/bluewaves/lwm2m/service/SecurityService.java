package com.asc.bluewaves.lwm2m.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.leshan.server.security.EditableSecurityStore;
import org.eclipse.leshan.server.security.NonUniqueSecurityInfoException;
import org.eclipse.leshan.server.security.SecurityInfo;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import com.asc.bluewaves.lwm2m.model.json.PublicKeySerDes;
import com.asc.bluewaves.lwm2m.model.json.SecurityDeserializer;
import com.asc.bluewaves.lwm2m.model.json.SecuritySerializer;
import com.asc.bluewaves.lwm2m.model.json.X509CertificateSerDes;
import com.eclipsesource.json.JsonObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import lombok.extern.slf4j.Slf4j;

@Service("SecurityService")
@Slf4j
public class SecurityService {

	private final EditableSecurityStore securityStore;
	private final PublicKey serverPublicKey;
	private final X509Certificate serverCertificate;

	private final X509CertificateSerDes certificateSerDes;
	private final PublicKeySerDes publicKeySerDes;

	private final Gson gsonSer;
	private final Gson gsonDes;

	public SecurityService(EditableSecurityStore securityStore, ObjectProvider<PublicKey> serverPublicKey,
			ObjectProvider<X509Certificate> serverCertificate) {
		this.securityStore = securityStore;
		this.serverPublicKey = serverPublicKey.getIfAvailable();
		this.serverCertificate = serverCertificate.getIfAvailable();
		certificateSerDes = new X509CertificateSerDes();
		publicKeySerDes = new PublicKeySerDes();

		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(SecurityInfo.class, new SecuritySerializer());
		gsonSer = builder.create();

		builder = new GsonBuilder();
		builder.registerTypeAdapter(SecurityInfo.class, new SecurityDeserializer());
		this.gsonDes = builder.create();
	}

	public void doUpdateRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String targetPath = req.getRequestURI().split("security")[1];
		String[] path = StringUtils.split(targetPath, '/');

		if (path.length != 1 && "clients".equals(path[0])) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			SecurityInfo info = gsonDes.fromJson(new InputStreamReader(req.getInputStream()), SecurityInfo.class);
			log.debug("New security info for end-point {}: {}", info.getEndpoint(), info);

			securityStore.add(info);

			resp.setStatus(HttpServletResponse.SC_OK);

		} catch (NonUniqueSecurityInfoException e) {
			log.warn("Non unique security info: " + e.getMessage());
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().append(e.getMessage()).flush();
		} catch (JsonParseException e) {
			log.warn("Could not parse request body", e);
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().append("Invalid request body").flush();
		} catch (RuntimeException e) {
			log.warn("unexpected error for request " + req.getPathInfo(), e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	public void doReadRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String targetPath = req.getRequestURI().split("security")[1];
		String[] path = StringUtils.split(targetPath, '/');

		if (path.length != 1) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		if ("clients".equals(path[0])) {
			Collection<SecurityInfo> infos = this.securityStore.getAll();

			String json = this.gsonSer.toJson(infos);
			resp.setContentType("application/json");
			resp.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
			resp.setStatus(HttpServletResponse.SC_OK);
			return;
		}

		if ("server".equals(path[0])) {
			JsonObject security = new JsonObject();
			if (serverPublicKey != null) {
				security.add("pubkey", publicKeySerDes.jSerialize(serverPublicKey));
			} else if (serverCertificate != null) {
				security.add("certificate", certificateSerDes.jSerialize(serverCertificate));
			}
			resp.setContentType("application/json");
			resp.getOutputStream().write(security.toString().getBytes(StandardCharsets.UTF_8));
			resp.setStatus(HttpServletResponse.SC_OK);
			return;
		}

		resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
	}

	public void doDeleteRequest(HttpServletRequest req, HttpServletResponse resp, String endpoint) throws ServletException, IOException {
		if (StringUtils.isEmpty(endpoint)) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		log.debug("Removing security info for end-point {}", endpoint);
		if (this.securityStore.remove(endpoint, true) != null) {
			resp.sendError(HttpServletResponse.SC_OK);
		} else {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
}
