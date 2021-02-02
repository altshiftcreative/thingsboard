package com.asc.bluewaves.lwm2m.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.asc.bluewaves.lwm2m.service.EventService;

@RestController
@RequestMapping("/lwm2m/event")
public class EventController extends BaseController {

	private final EventService eventService;

	EventController(EventService eventService) {
		this.eventService = eventService;
	}

	// /event/endPoint : register a LightWeight M2M event on a given client.
	@GetMapping(value = { "", "/*" })
	public void readRequest(HttpServletRequest request, HttpServletResponse response) {
		eventService.doGet(request, response);
	}
}
