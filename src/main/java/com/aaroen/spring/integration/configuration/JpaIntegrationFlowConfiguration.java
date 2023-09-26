package com.aaroen.spring.integration.configuration;

import com.aaroen.spring.integration.properties.IntegrationFlowProperties;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.jpa.core.JpaExecutor;
import org.springframework.integration.jpa.inbound.JpaPollingChannelAdapter;
import org.springframework.integration.scheduling.PollerMetadata;

import java.time.Duration;

@Configuration
@EnableIntegration
public class JpaIntegrationFlowConfiguration {
    private static final String CREATED_STATUS_EMAIL_QUERY = "SELECT e FROM EmailEntity e WHERE sendStatus = CREATED";
    private static final String PENDING_STATUS_EMAIL_QUERY = "SELECT e FROM EmailEntity e WHERE sendStatus = PENDING";

    @Bean
    public JpaExecutor jpaCreatedStatusEmailExecutor(EntityManager entityManager) {
        JpaExecutor jpaExecutor = new JpaExecutor(entityManager);
        jpaExecutor.setJpaQuery(CREATED_STATUS_EMAIL_QUERY);
        return jpaExecutor;
    }

    @Bean
    public JpaExecutor jpaPendingStatusEmailExecutor(EntityManager entityManager) {
        JpaExecutor jpaExecutor = new JpaExecutor(entityManager);
        jpaExecutor.setJpaQuery(PENDING_STATUS_EMAIL_QUERY);
        return jpaExecutor;
    }

    @Bean
    public JpaPollingChannelAdapter jpaCreatedStatusEmailPollingChannelAdapter(JpaExecutor jpaCreatedStatusEmailExecutor) {
        return new JpaPollingChannelAdapter(jpaCreatedStatusEmailExecutor);
    }

    @Bean
    public JpaPollingChannelAdapter jpaPendingStatusEmailPollingChannelAdapter(JpaExecutor jpaPendingStatusEmailExecutor) {
        return new JpaPollingChannelAdapter(jpaPendingStatusEmailExecutor);
    }

    @Bean
    public PollerMetadata jpaCreatedStatusEmailPollerMetadata(IntegrationFlowProperties integrationFlowProperties) {
        return Pollers.fixedDelay(Duration.ofSeconds(integrationFlowProperties.getCreatedStatusEmailPollerMetadataFixedDelayInSecond())).getObject();
    }

    @Bean
    public PollerMetadata jpaPendingStatusEmailPollerMetadata(IntegrationFlowProperties integrationFlowProperties) {
        return Pollers.fixedDelay(Duration.ofSeconds(integrationFlowProperties.getPendingStatusEmailPollerMetadataFixedDelayInSecond())).getObject();
    }

    @Bean
    public IntegrationFlow jpaCreatedStatusEmailPollerIntegrationFlow(JpaPollingChannelAdapter jpaCreatedStatusEmailPollingChannelAdapter,
                                                                      PollerMetadata jpaCreatedStatusEmailPollerMetadata) {
        return IntegrationFlow
                .from(jpaCreatedStatusEmailPollingChannelAdapter, e -> e.poller(jpaCreatedStatusEmailPollerMetadata))
                .split()
                .channel(IntegrationFlowConfiguration.CREATED_EMAIL_MESSAGE_CHANNEL)
                .get();
    }

    @Bean
    public IntegrationFlow jpaPendingStatusEmailPollerIntegrationFlow(JpaPollingChannelAdapter jpaPendingStatusEmailPollingChannelAdapter,
                                                                      PollerMetadata jpaPendingStatusEmailPollerMetadata) {
        return IntegrationFlow
                .from(jpaPendingStatusEmailPollingChannelAdapter, e -> e.poller(jpaPendingStatusEmailPollerMetadata))
                .split()
                .channel(IntegrationFlowConfiguration.PENDING_EMAIL_MESSAGE_CHANNEL)
                .get();
    }
}