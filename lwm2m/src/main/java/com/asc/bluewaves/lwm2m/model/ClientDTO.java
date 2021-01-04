package com.asc.bluewaves.lwm2m.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class ClientDTO implements Serializable {

	private String id;
	private String endpoint;
	
	private SecurityDTO security;
	private List<ServerDTO> servers;
	private List<DeviceDTO> devices;
	
//	"objectLinks": []

}
