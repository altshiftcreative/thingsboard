package com.asc.bluewaves.lwm2m.controller;

import org.eclipse.leshan.core.util.StringUtils;

public class BaseController {

	static final String INTERNAL_ERROR_MESSAGE = "Internal server error occured";
	static final String ENDPOINT = "endpoint";

	void checkParameter(String name, String param) throws NullPointerException {
		if (StringUtils.isEmpty(param)) {
			throw new NullPointerException("Parameter '" + name + "' can't be empty!");
		}
	}

}
