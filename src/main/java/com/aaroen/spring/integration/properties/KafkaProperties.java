package com.aaroen.spring.integration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring-integration.kafka")
public class KafkaProperties {
    private String bootstrapServers;
    private String emailNotificationTopic;
}