package com.aaroen.spring.integration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring-integration.flow")
public class IntegrationFlowProperties {
    private int createdEmailIntegrationThreadPool;
    private int pendingEmailIntegrationThreadPool;
    private int deliveryEmailIntegrationThreadPool;
    private int checkoutEmailIntegrationThreadPool;
    private int notificationEmailIntegrationThreadPool;
    private int createdStatusEmailPollerMetadataFixedDelayInSecond;
    private int pendingStatusEmailPollerMetadataFixedDelayInSecond;
}