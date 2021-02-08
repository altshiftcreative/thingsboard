package com.asc.bluewaves.lwm2m.model.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.asc.bluewaves.lwm2m.model.dto.ClientDTO;

import lombok.Data;

@Data
@Document(collection = "client")
public class Client implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
	private String id;
    
    @Indexed
	private String endpoint;
    
    @Field("access_token")
	private String accessToken;

	// PSK
	private String identity;
    
    @Field("pre_shared_key")
	private String preSharedKey;
    
    @Indexed
    private String owner;
	
    
    public Client() {
    	
	}
    
    public Client(ClientDTO dto) {
		endpoint = dto.getEndpoint();
		accessToken = dto.getAccessToken();
		identity = dto.getIdentity();
		preSharedKey = dto.getPreSharedKey();
		owner = dto.getOwner();
	}
}
