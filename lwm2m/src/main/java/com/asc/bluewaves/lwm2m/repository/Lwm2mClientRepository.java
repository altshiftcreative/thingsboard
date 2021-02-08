package com.asc.bluewaves.lwm2m.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.asc.bluewaves.lwm2m.model.domain.Client;

@Repository
public interface Lwm2mClientRepository extends MongoRepository<Client, String> {

	Page<Client> findByOwner(String owner, Pageable pageable);
	
	List<Client> findByOwner(String owner);
	
	Optional<Client> findByEndpoint(String endpoint);
	
	Optional<Client> findByIdentity(String identity);

	void deleteByEndpointAndOwner(String endpoint, String currentUsername);
	
}
