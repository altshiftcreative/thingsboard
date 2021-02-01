package com.asc.bluewaves.lwm2m.config;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.leshan.server.security.EditableSecurityStore;
import org.eclipse.leshan.server.security.NonUniqueSecurityInfoException;
import org.eclipse.leshan.server.security.SecurityInfo;
import org.eclipse.leshan.server.security.SecurityStoreListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.asc.bluewaves.lwm2m.model.dto.ClientDTO;
import com.asc.bluewaves.lwm2m.service.ClientMongodbService;

public class MongoSecurityStore implements EditableSecurityStore {

	private ClientMongodbService clientMongodbService;
	private SecurityStoreListener listener;

	public MongoSecurityStore(ClientMongodbService clientMongodbService) {
		this.clientMongodbService = clientMongodbService;
	}

	@Override
	public SecurityInfo getByEndpoint(String endpoint) {
		return toSecurityInfo(clientMongodbService.getByEndpoint(endpoint));
	}

	@Override
	public SecurityInfo getByIdentity(String pskIdentity) {
		return toSecurityInfo(clientMongodbService.getByIdentity(pskIdentity));
	}

	@Override
	public Collection<SecurityInfo> getAll() {
		return toSecurityInfos(clientMongodbService.getAllCurrentUserClients());
	}

	public Page<SecurityInfo> getAllCurrentUserClients(Pageable pageable) {
		return toSecurityInfoPage(clientMongodbService.getAllCurrentUserClients(pageable));
	}

	@Override
	public SecurityInfo add(SecurityInfo info) throws NonUniqueSecurityInfoException {
		return toSecurityInfo(clientMongodbService.addClient(new ClientDTO(info)));
	}

	@Override
	public SecurityInfo remove(String endpoint, boolean infosAreCompromised) {
		SecurityInfo info = getByEndpoint(endpoint);
		if (info != null) {

			clientMongodbService.removeClient(endpoint);

			if (listener != null) {
				listener.securityInfoRemoved(infosAreCompromised, info);
			}
		}
		return info;
	}

	@Override
	public void setListener(SecurityStoreListener listener) {
		this.listener = listener;
	}

	private SecurityInfo toSecurityInfo(ClientDTO dto) {
		if (dto.getEndpoint() == null || dto.getIdentity() == null || dto.getPreSharedKey() == null) {
			return null;
		}
		return SecurityInfo.newPreSharedKeyInfo(dto.getEndpoint(), dto.getIdentity(), dto.getPreSharedKey().getBytes());
	}

	private List<SecurityInfo> toSecurityInfos(List<ClientDTO> clients) {
		return clients.stream().map(dto -> toSecurityInfo(dto)).collect(Collectors.toList());
	}

	private Page<SecurityInfo> toSecurityInfoPage(Page<ClientDTO> page) {
		return page.map(dto -> toSecurityInfo(dto));
	}
}
