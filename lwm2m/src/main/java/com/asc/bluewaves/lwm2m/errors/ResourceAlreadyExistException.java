package com.asc.bluewaves.lwm2m.errors;

public class ResourceAlreadyExistException extends Exception {

	private static final long serialVersionUID = 1L;

	private final String message;

	public ResourceAlreadyExistException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
