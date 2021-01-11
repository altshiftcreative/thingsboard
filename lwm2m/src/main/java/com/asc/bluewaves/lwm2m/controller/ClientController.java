package com.asc.bluewaves.lwm2m.controller;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
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

import com.asc.bluewaves.lwm2m.errors.BadRequestException;
import com.asc.bluewaves.lwm2m.errors.ResourceAlreadyExistException;
import com.asc.bluewaves.lwm2m.model.ClientDTO;
import com.asc.bluewaves.lwm2m.service.ClientService;

@RestController
@RequestMapping("/api/clients")
public class ClientController extends BaseController {

	private final ClientService clientService;

	ClientController(ClientService clientService) {
		this.clientService = clientService;
	}

//	@GetMapping("/clients")
//	public ResponseEntity<Iterator<Registration>> getAllClients() {
//		try {
//			return ResponseEntity.ok(lwm2mService.getAllClients());
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new BadRequestException(INTERNAL_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, null);
//		}
//	}

//	@GetMapping("/clients/{" + ENDPOINT + "}")
//	public ResponseEntity<Registration> getClientByEndpoint(@PathVariable(ENDPOINT) String endpoint) {
//		try {
//			checkParameter(ENDPOINT, endpoint);
//			return ResponseEntity.ok(lwm2mService.getClientByEndpoint(endpoint));
//
//		} catch (NullPointerException e) {
//			e.printStackTrace();
//			throw new BadRequestException(e.getMessage(), HttpStatus.BAD_REQUEST, null);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new BadRequestException(INTERNAL_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, null);
//		}
//	}

//	@DeleteMapping("/{" + ENDPOINT + "}")
//	public ResponseEntity<Void> deleteDevice(@PathVariable(ENDPOINT) List<String> endpoints) {
//		// checkParameter(ENDPOINT, endpoint);
//		try {
//			clientService.deleteClient(endpoints);
//			return ResponseEntity.noContent().build();
//
//		} catch (NullPointerException e) {
//			e.printStackTrace();
//			throw new BadRequestException(e.getMessage(), HttpStatus.BAD_REQUEST, null);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new BadRequestException(INTERNAL_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, null);
//		}
//	}
	
	@DeleteMapping("")
	public ResponseEntity<Void> deleteDevices(@RequestBody List<String> endpoints) {
		try {
			clientService.deleteClient(endpoints);
			return ResponseEntity.noContent().build();

		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new BadRequestException(e.getMessage(), HttpStatus.BAD_REQUEST, null);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException(INTERNAL_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, null);
		}
	}

	@PostMapping("")
	public ResponseEntity<String> saveDevice(@RequestBody ClientDTO client) {
		try {
			clientService.saveClient(client);
			return ResponseEntity.created(new URI("/api/clients/" + client.getEndpoint())).build();

		} catch (ResourceAlreadyExistException bre) {
			bre.printStackTrace();
			throw new BadRequestException(bre.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException(INTERNAL_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, null);
		}
	}

	// /clients/endPoint/LWRequest : do LightWeight M2M read request on a given client.
	// GET //localhost:8080/api/clients/ebraheem-fedora/1/0?format=TLV&timeout=5
	@GetMapping(value = { "", "/{" + ENDPOINT + "}/**" })
	public void readRequest(@PathVariable(value = ENDPOINT, required = false) String endpoint,
			@RequestParam(value = "format", required = false) String format,
			@RequestParam(value = "timeout", defaultValue = "5") Integer timeout, HttpServletRequest request,
			HttpServletResponse response) {

		try {
			clientService.doReadRequest(request, response, format, timeout);

		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new BadRequestException(e.getMessage(), HttpStatus.BAD_REQUEST, null);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException(INTERNAL_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, null);
		}
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
		try {
			clientService.doUpdateRequest(request, response, replace, format, timeout);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new BadRequestException(e.getMessage(), HttpStatus.BAD_REQUEST, null);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException(INTERNAL_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, null);
		}
	}

	// /clients/endPoint/LWRequest : do LightWeight M2M create request on a given client.
	// Post //localhost:8080/api/clients/ebraheem-fedora/1/0?format=TLV&timeout=5
	@PostMapping("/{" + ENDPOINT + "}/**")
	public void postRequest(@PathVariable(ENDPOINT) String endpoint, @RequestParam(value = "format", required = false) String format,
			@RequestParam(value = "timeout", defaultValue = "5") Integer timeout, HttpServletRequest request,
			HttpServletResponse response) {

		checkParameter(ENDPOINT, endpoint);
		try {
			clientService.doPostRequest(request, response, format, timeout);

		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new BadRequestException(e.getMessage(), HttpStatus.BAD_REQUEST, null);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException(INTERNAL_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, null);
		}
	}

	// /clients/endPoint/LWRequest : do LightWeight M2M delete request on a given client.
	// Delete //localhost:8080/api/clients/ebraheem-fedora/1/0?format=TLV&timeout=5
	@DeleteMapping("/{" + ENDPOINT + "}/**")
	public void deleteRequest(@PathVariable(ENDPOINT) String endpoint, @RequestParam(value = "timeout", defaultValue = "5") Integer timeout,
			HttpServletRequest request, HttpServletResponse response) {

		checkParameter(ENDPOINT, endpoint);
		try {
			clientService.doDeleteRequest(request, response, timeout);

		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new BadRequestException(e.getMessage(), HttpStatus.BAD_REQUEST, null);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException(INTERNAL_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, null);
		}
	}

}
