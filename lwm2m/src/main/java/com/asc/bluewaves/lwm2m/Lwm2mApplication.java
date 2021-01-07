package com.asc.bluewaves.lwm2m;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.asc.bluewaves.lwm2m")
public class Lwm2mApplication {

	public static void main(String[] args) {
		SpringApplication.run(Lwm2mApplication.class, args);
	}
}
