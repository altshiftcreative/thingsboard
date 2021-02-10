package com.asc.bluewaves.lwm2m.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.asc.bluewaves.lwm2m.model.dto.ClientDTO;
import com.asc.bluewaves.lwm2m.service.ClientMongodbService;
import com.asc.bluewaves.lwm2m.service.ClientServerService;

@RestController
@RequestMapping("/lwm2m/api/clients")
public class ClientController extends BaseController {

	private final ClientServerService clientServerService;
	private final ClientMongodbService clientMongodbService;

	ClientController(ClientServerService clientServerService, ClientMongodbService clientMongodbService) {
		this.clientServerService = clientServerService;
		this.clientMongodbService = clientMongodbService;
	}

	@DeleteMapping("")
	public ResponseEntity<Void> deleteDevices(@RequestBody List<String> endpoints) {
		clientMongodbService.removeClient(endpoints);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("")
	public ResponseEntity<String> saveDevice(@RequestBody ClientDTO client) throws URISyntaxException {
		clientMongodbService.addClient(client);
		return ResponseEntity.created(new URI("/api/clients/" + client.getEndpoint())).build(); 
	}

	@GetMapping("/{" + ENDPOINT + "}/run")
	public ResponseEntity<Void> runDevice(@PathVariable(ENDPOINT) String endpoint) {
		clientMongodbService.runClient(endpoint);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/demo")
	public ResponseEntity<Void> createDemoDevices(@RequestParam(value = "devicesNo", defaultValue = "1") Integer devicesNo) {
		clientMongodbService.createDemoDevices(devicesNo);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/demo/observer")
	public ResponseEntity<Void> createObserverForDemoDevices(@RequestParam(value = "devicesNo", defaultValue = "1") Integer devicesNo) {
		clientServerService.createObserverForDemoDevices(devicesNo);
		return ResponseEntity.noContent().build();
	}
	
	// /clients/endPoint/LWRequest : do LightWeight M2M read request on a given client.
	// GET //localhost:8080/api/clients/ebraheem-fedora/1/0?format=TLV&timeout=5
	@GetMapping(value = { "", "/{" + ENDPOINT + "}/**" })
	public void readRequest(@PathVariable(value = ENDPOINT, required = false) String endpoint,
			@RequestParam(value = "format", required = false) String format,
			@RequestParam(value = "timeout", defaultValue = "5") Integer timeout, HttpServletRequest request,
			HttpServletResponse response) {

		clientServerService.doReadRequest(request, response, format, timeout);
	}

	// at least /endpoint/objectId/instanceId : do LightWeight M2M update or replcae request on a given client.
	// PUT //localhost:8080/api/clients/ebraheem-fedora/1/0/7?format=TLV&timeout=5
	@PutMapping("/{" + ENDPOINT + "}/**")
	public void updateRequest(@PathVariable(ENDPOINT) String endpoint,
			@RequestParam(value = "replace", required = false, defaultValue = "true") Boolean replace,
			@RequestParam(value = "format", required = false) String format,
			@RequestParam(value = "timeout", defaultValue = "5") Integer timeout, HttpServletRequest request,
			HttpServletResponse response) {

		checkParameter(ENDPOINT, endpoint);
		clientServerService.doUpdateRequest(request, response, replace, format, timeout);
	}

	// /clients/endPoint/LWRequest : do LightWeight M2M create request on a given client.
	// Post //localhost:8080/api/clients/ebraheem-fedora/1/0?format=TLV&timeout=5
	@PostMapping("/{" + ENDPOINT + "}/**")
	public void postRequest(@PathVariable(ENDPOINT) String endpoint, @RequestParam(value = "format", required = false) String format,
			@RequestParam(value = "timeout", defaultValue = "5") Integer timeout, HttpServletRequest request,
			HttpServletResponse response) {

		checkParameter(ENDPOINT, endpoint);
		clientServerService.doPostRequest(request, response, format, timeout);
	}

	// /clients/endPoint/LWRequest : do LightWeight M2M delete request on a given client.
	// Delete //localhost:8080/api/clients/ebraheem-fedora/1/0?format=TLV&timeout=5
	@DeleteMapping("/{" + ENDPOINT + "}/**")
	public void deleteRequest(@PathVariable(ENDPOINT) String endpoint, @RequestParam(value = "timeout", defaultValue = "5") Integer timeout,
			HttpServletRequest request, HttpServletResponse response) {

		checkParameter(ENDPOINT, endpoint);
		clientServerService.doDeleteRequest(request, response, timeout);

	}

}
