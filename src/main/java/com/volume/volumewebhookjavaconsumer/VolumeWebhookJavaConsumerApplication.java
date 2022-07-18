package com.volume.volumewebhookjavaconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class VolumeWebhookJavaConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(VolumeWebhookJavaConsumerApplication.class, args);
    }

}
