package com.asc.bluewaves.lwm2m.service;

import java.util.Optional;

import com.asc.bluewaves.lwm2m.security.SecurityUtils;

public class BaseService {

	String getCurrentToken() {
		Optional<String> opt = SecurityUtils.getCurrentUserJWT();
		if (opt.isPresent()) {
			return opt.get();
		}
		return "";
	}
	
	String getCurrentUsername() {
		Optional<String> opt = SecurityUtils.getCurrentUserLogin();
		if (opt.isPresent()) {
			return opt.get();
		}
		return "anonymous";
	}
}
