package com.volume.volumewebhookjavaconsumer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "volume")
@ConstructorBinding
public record ApplicationProperties(String pemUrl) {
}
