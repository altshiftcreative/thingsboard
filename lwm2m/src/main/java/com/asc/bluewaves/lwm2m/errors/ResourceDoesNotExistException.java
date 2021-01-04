package com.asc.bluewaves.lwm2m.errors;

public class ResourceDoesNotExistException extends Exception {

	private static final long serialVersionUID = 1L;

	private final String message;

	public ResourceDoesNotExistException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
