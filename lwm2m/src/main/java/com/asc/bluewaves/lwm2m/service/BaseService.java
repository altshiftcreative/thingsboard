package com.asc.bluewaves.lwm2m.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.asc.bluewaves.lwm2m.security.SecurityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BaseService {

	@Value("${tb.base-url}")
	private String tbBaseUrl;

	@Value("${tb.username}")
	private String tbUsername;

	@Value("${tb.password}")
	private String tbPassword;

	@Value("${tb.provision.key}")
	private String tbProvisionKey;

	@Value("${tb.provision.secret}")
	private String tbProvisionSecret;

	private final ObjectMapper mapper = new ObjectMapper();

	String getNewAccessToken(String endpoint) {
		String loginUrl = tbBaseUrl + "/auth/login";
		String accessTokenUrl = tbBaseUrl + "/v1/provision";

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// First request, get token
		Map<String, String> body = new HashMap<>();
		body.put("username", tbUsername);
		body.put("password", tbPassword);

		try {
			HttpEntity<String> request = new HttpEntity<String>(mapper.writeValueAsString(body), headers);
			ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, request, String.class);

			JsonNode root = mapper.readTree(response.getBody());
			String token = root.findValue("token").asText("");

			// Second request, get access token
			headers.set("Authorization", "Bearer " + token);

			body.clear();
			body.put("deviceName", endpoint);
			body.put("provisionDeviceKey", tbProvisionKey);
			body.put("provisionDeviceSecret", tbProvisionSecret);

			request = new HttpEntity<String>(mapper.writeValueAsString(body), headers);
			response = restTemplate.postForEntity(accessTokenUrl, request, String.class);

			root = mapper.readTree(response.getBody());

			if (root.findValue("credentialsValue") == null) {
				return "";
			}
			return root.findValue("credentialsValue").asText();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	String sendTelemetryData(String endpoint) {
		String loginUrl = tbBaseUrl + "/auth/login";
		String accessTokenUrl = tbBaseUrl + "/v1/provision";

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// First request, get token
		Map<String, String> body = new HashMap<>();
		body.put("username", tbUsername);
		body.put("password", tbPassword);

		try {
			HttpEntity<String> request = new HttpEntity<String>(mapper.writeValueAsString(body), headers);
			ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, request, String.class);

			JsonNode root = mapper.readTree(response.getBody());
			String token = root.findValue("token").asText("");

			// Second request, get access token
			headers.set("Authorization", "Bearer " + token);

			body.clear();
			body.put("deviceName", endpoint);
			body.put("provisionDeviceKey", tbProvisionKey);
			body.put("provisionDeviceSecret", tbProvisionSecret);

			request = new HttpEntity<String>(mapper.writeValueAsString(body), headers);
			response = restTemplate.postForEntity(accessTokenUrl, request, String.class);

			root = mapper.readTree(response.getBody());

			if (root.findValue("credentialsValue") == null) {
				return "";
			}
			return root.findValue("credentialsValue").asText();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	String getCurrentUsername() {
		Optional<String> opt = SecurityUtils.getCurrentUserLogin();
		if (opt.isPresent()) {
			return opt.get();
		}
		return "anonymous";
	}
}
