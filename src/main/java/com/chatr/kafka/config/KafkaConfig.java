package com.chatr.kafka.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("spring.kafka")
@Data
public class KafkaConfig {
    private String bootstrapServers;
}
