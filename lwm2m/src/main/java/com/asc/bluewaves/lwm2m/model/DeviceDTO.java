package com.asc.bluewaves.lwm2m.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class DeviceDTO implements Serializable {

	// Device Data
	private String manufacturer;
    private String modelNumber;
    private String serialNumber;
    private String supportedBinding;
    
}
