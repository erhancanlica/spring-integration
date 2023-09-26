package com.aaroen.spring.integration.integration.gateway;

import com.aaroen.spring.integration.configuration.IntegrationFlowConfiguration;
import com.aaroen.spring.integration.persistence.entity.EmailEntity;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface EmailNotificationGateway {
    @Gateway(requestChannel = IntegrationFlowConfiguration.NOTIFICATION_EMAIL_MESSAGE_CHANNEL)
    void process(EmailEntity emailEntity);
}