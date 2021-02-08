package com.asc.bluewaves.lwm2m.exception;

import org.eclipse.leshan.core.node.codec.CodecException;
import org.eclipse.leshan.core.request.exception.ClientSleepingException;
import org.eclipse.leshan.core.request.exception.InvalidRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.asc.bluewaves.lwm2m.exception.errors.BadRequestException;
import com.asc.bluewaves.lwm2m.exception.errors.ResourceAlreadyExistException;
import com.asc.bluewaves.lwm2m.exception.errors.ResourceDoesNotExistException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ExceptionHelper {

	// TODO: we can create log service to save every error in a database
	// Also we can make multi-language messages service
	static private final String INTERNAL_ERROR_MESSAGE = "Internal server error occured";

	@ExceptionHandler(value = { InvalidRequestException.class, CodecException.class, ClientSleepingException.class,
			ResourceAlreadyExistException.class, ResourceDoesNotExistException.class, BadRequestException.class })
	public ResponseEntity<ErrorObject> handleBadequestException(Exception ex) {
		log.error("Invalid Request Exception: ", ex.getMessage());
		ex.printStackTrace();
		return new ResponseEntity<ErrorObject>(new ErrorObject(ex.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	}

//	@ExceptionHandler(value = { Unauthorized.class })
//	public ResponseEntity<ErrorObject> handleUnauthorizedException(Unauthorized ex) {
//		log.error("Unauthorized Exception: ", ex.getMessage());
//		return new ResponseEntity<ErrorObject>(new ErrorObject(ex.getMessage(), HttpStatus.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
//	}

//	@ExceptionHandler(value = { ResourceAlreadyExistException.class })
//	public ResponseEntity<ErrorObject> handleDuplicateException(ResourceAlreadyExistException ex) {
//		log.error("Business Exception: ", ex.getMessage());
//		return new ResponseEntity<ErrorObject>(new ErrorObject(ex.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
//	}

	@ExceptionHandler(value = { RuntimeException.class, Exception.class })
	public ResponseEntity<ErrorObject> handleException(Exception ex) {
		log.error("Exception: ", ex.getMessage());
		ex.printStackTrace();
		return new ResponseEntity<ErrorObject>(new ErrorObject(INTERNAL_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
