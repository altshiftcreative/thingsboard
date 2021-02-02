package com.asc.bluewaves.lwm2m.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.asc.bluewaves.lwm2m.exception.errors.BadRequestException;
import com.asc.bluewaves.lwm2m.service.SecurityService;

@RestController
@RequestMapping("/lwm2m/api/security")
public class SecurityController extends BaseController {

	private final SecurityService securityService;

	SecurityController(SecurityService securityService) {
		this.securityService = securityService;
	}

	@GetMapping("/**")
	public void readRequest(HttpServletRequest request, HttpServletResponse response) {
		securityService.doReadRequest(request, response);
	}

	@PutMapping("/**")
	public void updateRequest(HttpServletRequest request, HttpServletResponse response) {
		securityService.doUpdateRequest(request, response);
	}

	@DeleteMapping("/clients/{" + ENDPOINT + "}")
	public void deleteRequest(@PathVariable(ENDPOINT) String endpoint, HttpServletRequest request, HttpServletResponse response) {
		securityService.doDeleteRequest(request, response, endpoint);
	}
}
