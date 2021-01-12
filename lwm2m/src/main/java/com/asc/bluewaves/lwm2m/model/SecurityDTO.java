package com.asc.bluewaves.lwm2m.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class SecurityDTO implements Serializable {

	private String type;
	private String pskIdentity;
	private String pskKey;
	private Integer shortServerId;

}
