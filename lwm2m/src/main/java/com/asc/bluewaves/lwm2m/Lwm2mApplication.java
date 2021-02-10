package com.asc.bluewaves.lwm2m;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import com.asc.bluewaves.lwm2m.config.ApplicationProperties;

@SpringBootApplication(exclude= {UserDetailsServiceAutoConfiguration.class})
@EnableConfigurationProperties({ApplicationProperties.class})
public class Lwm2mApplication {

	public static void main(String[] args) {
		SpringApplication.run(Lwm2mApplication.class, args);
	}
}
