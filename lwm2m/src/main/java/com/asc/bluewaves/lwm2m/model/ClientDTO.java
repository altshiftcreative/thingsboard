package com.asc.bluewaves.lwm2m.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class ClientDTO implements Serializable {

	//private String id;
	private String endpoint;
	private String accessToken;

}
