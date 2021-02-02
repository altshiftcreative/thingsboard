package com.asc.bluewaves.lwm2m.model.dto;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import org.eclipse.leshan.server.security.SecurityInfo;

import com.asc.bluewaves.lwm2m.model.domain.Client;

import lombok.Data;

@Data
public class ClientDTO implements Serializable {

	//private String id;
	private String endpoint;
	private String accessToken;
	
	// PSK
	private String identity;
	private String preSharedKey;
    
    private String owner;
    
    
    public ClientDTO() {
    	
	}
    
	public ClientDTO(Client client) {
		endpoint = client.getEndpoint();
		accessToken = client.getAccessToken();
		identity = client.getIdentity();
		preSharedKey = client.getPreSharedKey();
		owner = client.getOwner();
	}
	
	public ClientDTO(SecurityInfo info) {
		endpoint = info.getEndpoint();
		identity = info.getIdentity();
		preSharedKey = new String(info.getPreSharedKey(), StandardCharsets.UTF_8);;
	}
}
