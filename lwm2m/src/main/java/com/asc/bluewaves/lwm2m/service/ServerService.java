package com.asc.bluewaves.lwm2m.service;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.annotation.PreDestroy;

import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.leshan.core.model.InvalidDDFFileException;
import org.eclipse.leshan.core.model.InvalidModelException;
import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.node.codec.DefaultLwM2mNodeDecoder;
import org.eclipse.leshan.core.node.codec.DefaultLwM2mNodeEncoder;
import org.eclipse.leshan.core.node.codec.LwM2mNodeDecoder;
import org.eclipse.leshan.core.util.SecurityUtil;
import org.eclipse.leshan.server.californium.LeshanServer;
import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.model.LwM2mModelProvider;
import org.eclipse.leshan.server.model.VersionedModelProvider;
import org.eclipse.leshan.server.security.EditableSecurityStore;
import org.eclipse.leshan.server.security.FileSecurityStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import com.asc.bluewaves.lwm2m.converter.MagicLwM2mValueConverter;
import com.asc.bluewaves.lwm2m.model.LwM2mDemoConstant;
import com.asc.bluewaves.lwm2m.service.event.EventService;

import lombok.extern.slf4j.Slf4j;

@Service("ServerService")
@Slf4j
public class ServerService {

	private LeshanServer server;
	private X509Certificate serverCertificate;

	// private final int PORT = 5685;
	private final int SEC_PORT = 5686;

	// get the Redis hostname:port

	ServerService() {
		startLwm2mServer();
	}

	public void startLwm2mServer() {
		log.info("Starting LWM2M transport server....");

		LeshanServerBuilder builder = new LeshanServerBuilder();

		// use a magic converter to support bad type send by the UI.
		builder.setEncoder(new DefaultLwM2mNodeEncoder(new MagicLwM2mValueConverter()));
		builder.setDecoder(new DefaultLwM2mNodeDecoder());

		// Create CoAP Config
		NetworkConfig coapConfig;
		File configFile = new File(NetworkConfig.DEFAULT_FILE_NAME);
		if (configFile.isFile()) {
			coapConfig = new NetworkConfig();
			coapConfig.load(configFile);
		} else {
			coapConfig = LeshanServerBuilder.createDefaultNetworkConfig();
			coapConfig.store(configFile);
		}
		builder.setCoapConfig(coapConfig);

		// builder.setLocalAddress("localhost", PORT); // Thingsboard CoAP will reserve the default port
		builder.setLocalSecureAddress("localhost", SEC_PORT); // Thingsboard CoAP will reserve the default port
		builder.disableUnsecuredEndpoint();

		// TODO: we have to implement our security store that save data to mongoDB
		EditableSecurityStore securityStore = new FileSecurityStore();
		builder.setSecurityStore(securityStore);

		// public key or server certificated is not defined
		// use default embedded credentials (X.509 + RPK mode)
		try {
			PrivateKey embeddedPrivateKey = SecurityUtil.privateKey.readFromResource("credentials/server_privkey.der");
			serverCertificate = SecurityUtil.certificate.readFromResource("credentials/server_cert.der");
			builder.setPrivateKey(embeddedPrivateKey);
			builder.setCertificateChain(new X509Certificate[] { serverCertificate });
		} catch (Exception e) {
			log.error("Unable to load embedded X.509 certificate.", e);
			System.exit(-1);
		}

		// Create DTLS Config
		DtlsConnectorConfig.Builder dtlsConfig = new DtlsConnectorConfig.Builder();
		dtlsConfig.setRecommendedCipherSuitesOnly(true);
		builder.setDtlsConfig(dtlsConfig);

		// Load s
		// Define model provider
		List<ObjectModel> models = ObjectLoader.loadAllDefault();
		try {
			models.addAll(ObjectLoader.loadDdfResources("/models/", LwM2mDemoConstant.modelPaths));
			LwM2mModelProvider modelProvider = new VersionedModelProvider(models);
			builder.setObjectModelProvider(modelProvider);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidModelException e) {
			e.printStackTrace();
		} catch (InvalidDDFFileException e) {
			e.printStackTrace();
		}

		this.server = builder.build();

		server.start();

		// Add event listeners
		new EventService(server);

		log.info("LWM2M transport started!");
	}

	@PreDestroy
	public void shutdown() {
		log.info("Stopping LWM2M transport!");
		if (server != null) {
			server.destroy();
		}
		log.info("LWM2M transport stopped!");
	}

	@Bean(name = "server")
	public LeshanServer getServer() {
		if (server == null) {
			startLwm2mServer();
		}
		return server;
	}

	@Bean(name = "securityStore")
	@DependsOn(value = "server")
	public EditableSecurityStore getSecurityStore() {
		return (EditableSecurityStore) server.getSecurityStore();
	}

	@Bean(name = "serverCertificate")
	@DependsOn(value = "server")
	public X509Certificate serverCertificate() {
		return serverCertificate;
	}

	@Bean(name = "serverPublicKey")
	@DependsOn(value = "server")
	public PublicKey serverPublicKey() {
		return null;
	}

//	private final RegistrationListener registrationListener = new RegistrationListener() {
//
//		public void registered(Registration registration, Registration previousReg, Collection<Observation> previousObsersations) {
//			System.out.println("Hi EBRAHEEM, a new device entered: " + registration.getEndpoint());
////			try {
////				Bson filter = and(eq("endpoint", registration.getEndpoint()));
////				Iterable<Document> client = mongoTemplate.getCollection("client").find(filter);
////
////				if (client.iterator().hasNext()) {
////					// this client in our DB
////					// TODO: Save new links in the client DTO
////
////				} else {
////					// this client is not in our DB
////					// Find a way to kick out clients not exist in DB and log the event
////				}
////			} catch (Exception e) {
////				e.printStackTrace();
////			}
//		}
//
//		public void updated(RegistrationUpdate update, Registration updatedReg, Registration previousReg) {
//			System.out.println("Hi EBRAHEEM, a device is still here: " + updatedReg.getEndpoint());
//		}
//
//		public void unregistered(Registration registration, Collection<Observation> observations, boolean expired, Registration newReg) {
//			System.out.println("Hi EBRAHEEM, a device left: " + registration.getEndpoint());
//		}
//	};
}
