package com.asc.bluewaves.lwm2m.exception;

import java.util.Date;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class ErrorObject {

	private String logId;
    private String errorCode;
    private String error;
    private String message;
    private int status;
    private Date timestamp;
    
//    "path": "/lwm2m/api/clients"
    
    ErrorObject(String message, HttpStatus httpStatus) {
    	this.message = message;
    	this.status = httpStatus.value();
    	this.error = httpStatus.getReasonPhrase();
    	this.timestamp = new Date();
    }
}
