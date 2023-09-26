package com.aaroen.spring.integration.integration.gateway;

import com.aaroen.spring.integration.configuration.IntegrationFlowConfiguration;
import com.aaroen.spring.integration.persistence.entity.EmailEntity;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface EmailDeliveryGateway {
    @Gateway(requestChannel = IntegrationFlowConfiguration.DELIVERY_EMAIL_MESSAGE_CHANNEL)
    void process(final EmailEntity emailEntity);
}