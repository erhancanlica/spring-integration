package com.aaroen.spring.integration.configuration;

import com.aaroen.spring.integration.integration.gateway.EmailCheckoutGateway;
import com.aaroen.spring.integration.integration.handler.CheckoutEmailMessageHandler;
import com.aaroen.spring.integration.integration.handler.DeliveryEmailMessageHandler;
import com.aaroen.spring.integration.integration.handler.CreateEmailMessageHandler;
import com.aaroen.spring.integration.properties.IntegrationFlowProperties;
import com.aaroen.spring.integration.properties.KafkaProperties;
import com.aaroen.spring.integration.service.EmailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessageChannel;

import java.util.concurrent.Executors;

@Configuration
public class IntegrationFlowConfiguration {
    public static final String CREATED_EMAIL_MESSAGE_CHANNEL = "createdEmailMessageChannel";
    public static final String PENDING_EMAIL_MESSAGE_CHANNEL = "pendingEmailMessageChannel";
    public static final String DELIVERY_EMAIL_MESSAGE_CHANNEL = "deliveryEmailMessageChannel";
    public static final String CHECKOUT_EMAIL_MESSAGE_CHANNEL = "checkoutEmailMessageChannel";
    public static final String NOTIFICATION_EMAIL_MESSAGE_CHANNEL = "notificationEmailMessageChannel";

    @Bean(name = IntegrationFlowConfiguration.CREATED_EMAIL_MESSAGE_CHANNEL)
    public MessageChannel createdEmailMessageChannel() {
        return new QueueChannel(Integer.MAX_VALUE);
    }

    @Bean(name = IntegrationFlowConfiguration.PENDING_EMAIL_MESSAGE_CHANNEL)
    public MessageChannel pendingEmailMessageChannel() {
        return new QueueChannel(Integer.MAX_VALUE);
    }

    @Bean(name = IntegrationFlowConfiguration.DELIVERY_EMAIL_MESSAGE_CHANNEL)
    public MessageChannel deliveryEmailMessageChannel() {
        return new QueueChannel(Integer.MAX_VALUE);
    }

    @Bean(name = IntegrationFlowConfiguration.CHECKOUT_EMAIL_MESSAGE_CHANNEL)
    public MessageChannel checkoutEmailMessageChannel() {
        return new QueueChannel(Integer.MAX_VALUE);
    }

    @Bean(name = IntegrationFlowConfiguration.NOTIFICATION_EMAIL_MESSAGE_CHANNEL)
    public MessageChannel notificationEmailMessageChannel() {
        return new QueueChannel(Integer.MAX_VALUE);
    }

    @Bean
    public CreateEmailMessageHandler createdEmailMessageHandler(EmailService emailService) {
        return new CreateEmailMessageHandler(emailService);
    }

    @Bean
    public DeliveryEmailMessageHandler deliveryEmailMessageHandler(EmailService emailService,
                                                                   EmailCheckoutGateway emailCheckoutGateway) {
        return new DeliveryEmailMessageHandler(emailService, emailCheckoutGateway);
    }

    @Bean
    public CheckoutEmailMessageHandler checkoutEmailMessageHandler(EmailService emailService) {
        return new CheckoutEmailMessageHandler(emailService);
    }

    @Bean
    public IntegrationFlow createdEmailIntegrationFlow(MessageChannel createdEmailMessageChannel,
                                                       CreateEmailMessageHandler createdEmailMessageHandler,
                                                       IntegrationFlowProperties integrationFlowProperties) {
        return IntegrationFlow
                .from(createdEmailMessageChannel)
                .channel(MessageChannels.executor(Executors.newFixedThreadPool(integrationFlowProperties.getCreatedEmailIntegrationThreadPool())))
                .handle(createdEmailMessageHandler)
                .get();
    }

    @Bean
    public IntegrationFlow pendingEmailIntegrationFlow(MessageChannel pendingEmailMessageChannel,
                                                       DeliveryEmailMessageHandler deliveryEmailMessageHandler,
                                                       IntegrationFlowProperties integrationFlowProperties) {
        return IntegrationFlow
                .from(pendingEmailMessageChannel)
                .channel(MessageChannels.executor(Executors.newFixedThreadPool(integrationFlowProperties.getPendingEmailIntegrationThreadPool())))
                .handle(deliveryEmailMessageHandler)
                .get();
    }

    @Bean
    public IntegrationFlow deliveryEmailIntegrationFlow(MessageChannel deliveryEmailMessageChannel,
                                                        DeliveryEmailMessageHandler deliveryEmailMessageHandler,
                                                        IntegrationFlowProperties integrationFlowProperties) {
        return IntegrationFlow
                .from(deliveryEmailMessageChannel)
                .channel(MessageChannels.executor(Executors.newFixedThreadPool(integrationFlowProperties.getDeliveryEmailIntegrationThreadPool())))
                .handle(deliveryEmailMessageHandler)
                .get();
    }

    @Bean
    public IntegrationFlow checkoutEmailIntegrationFlow(MessageChannel checkoutEmailMessageChannel,
                                                        CheckoutEmailMessageHandler checkoutEmailMessageHandler,
                                                        IntegrationFlowProperties integrationFlowProperties) {
        return IntegrationFlow
                .from(checkoutEmailMessageChannel)
                .channel(MessageChannels.executor(Executors.newFixedThreadPool(integrationFlowProperties.getCheckoutEmailIntegrationThreadPool())))
                .handle(checkoutEmailMessageHandler)
                .get();
    }

    @Bean
    public IntegrationFlow notificationEmailIntegrationFlow(MessageChannel notificationEmailMessageChannel,
                                                            IntegrationFlowProperties integrationFlowProperties,
                                                            KafkaProperties kafkaProperties,
                                                            KafkaTemplate<String, Object> kafkaTemplate) {
        return IntegrationFlow
                .from(notificationEmailMessageChannel)
                .channel(MessageChannels.executor(Executors.newFixedThreadPool(integrationFlowProperties.getNotificationEmailIntegrationThreadPool())))
                .handle(message -> kafkaTemplate.send(kafkaProperties.getEmailNotificationTopic(), message))
                .get();
    }
}
