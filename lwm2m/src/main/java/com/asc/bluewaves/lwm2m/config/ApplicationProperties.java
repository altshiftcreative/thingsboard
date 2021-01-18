package com.asc.bluewaves.lwm2m.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to leshan.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
}
