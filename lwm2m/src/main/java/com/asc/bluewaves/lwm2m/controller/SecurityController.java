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

import com.asc.bluewaves.lwm2m.errors.BadRequestException;
import com.asc.bluewaves.lwm2m.service.SecurityService;

@RestController
@RequestMapping("/api/security")
public class SecurityController extends BaseController {

	private final SecurityService securityService;

	SecurityController(SecurityService securityService) {
		this.securityService = securityService;
	}

	@GetMapping("/**")
	public void readRequest(HttpServletRequest request, HttpServletResponse response) {

		try {
			securityService.doReadRequest(request, response);

		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new BadRequestException(e.getMessage(), HttpStatus.BAD_REQUEST, null);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException(INTERNAL_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, null);
		}
	}

	@PutMapping("")
	public void updateRequest(HttpServletRequest request, HttpServletResponse response) {
		try {
			securityService.doUpdateRequest(request, response);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new BadRequestException(e.getMessage(), HttpStatus.BAD_REQUEST, null);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException(INTERNAL_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, null);
		}
	}

	@DeleteMapping("/clients/{" + ENDPOINT + "}")
	public void deleteRequest(@PathVariable(ENDPOINT) String endpoint, HttpServletRequest request,
			HttpServletResponse response) {

		try {
			securityService.doDeleteRequest(request, response, endpoint);

		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new BadRequestException(e.getMessage(), HttpStatus.BAD_REQUEST, null);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException(INTERNAL_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, null);
		}
	}
}
