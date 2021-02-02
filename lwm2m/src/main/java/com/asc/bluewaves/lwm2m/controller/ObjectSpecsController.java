package com.asc.bluewaves.lwm2m.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.asc.bluewaves.lwm2m.service.ObjectSpecsService;

@RestController
@RequestMapping("/lwm2m/api/objectspecs")
public class ObjectSpecsController extends BaseController {

	private final ObjectSpecsService objSpecsService;

	ObjectSpecsController(ObjectSpecsService objSpecsService) {
		this.objSpecsService = objSpecsService;
	}

	// Get object specifications for a given client
	// GET //localhost:8080/api/objectspecs/ebraheem-fedora
	@GetMapping("/{" + ENDPOINT + "}")
	public void getClientObjectSpec(@PathVariable(ENDPOINT) String endpoint, HttpServletRequest request, HttpServletResponse response) {
		checkParameter(ENDPOINT, endpoint);
		objSpecsService.getClientObjectSpec(request, response, endpoint);
	}

}
