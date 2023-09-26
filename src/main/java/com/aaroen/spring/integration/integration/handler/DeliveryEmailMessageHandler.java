package com.aaroen.spring.integration.integration.handler;

import com.aaroen.spring.integration.integration.gateway.EmailCheckoutGateway;
import com.aaroen.spring.integration.model.enums.SendStatusEnum;
import com.aaroen.spring.integration.persistence.entity.EmailEntity;
import com.aaroen.spring.integration.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

@Slf4j
@RequiredArgsConstructor
public class DeliveryEmailMessageHandler implements MessageHandler {
    private final EmailService emailService;
    private final EmailCheckoutGateway emailCheckoutGateway;

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        final EmailEntity messagePayload = (EmailEntity) message.getPayload();
        log.info("The email {} has arrived to delivery email message handler.", messagePayload.getId());

        String emailSendStatus = emailService.send(messagePayload);
        if (emailSendStatus.equals(SendStatusEnum.SUCCESSFUL.getMessage())) {
            emailCheckoutGateway.process(messagePayload);
        }
    }
}
