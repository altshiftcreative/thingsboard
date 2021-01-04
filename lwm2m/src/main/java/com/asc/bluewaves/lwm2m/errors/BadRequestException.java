package com.asc.bluewaves.lwm2m.errors;

import org.springframework.http.HttpStatus;

public class BadRequestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String message;
	private final String errorKey;
	private final HttpStatus status;

	public BadRequestException(String message, HttpStatus status, String errorKey) {
		this.message = message;
		this.errorKey = errorKey;
		this.status = status;
	}

	public String getErrorKey() {
		return errorKey;
	}

	public String getMessage() {
		return message;
	}

	public HttpStatus getStatus() {
		return status;
	}

//    private static Map<String, Object> getAlertParameters(String entityName, String errorKey) {
//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("message", "error." + errorKey);
//        parameters.put("params", entityName);
//        return parameters;
//    }

}
