package com.aaroen.spring.integration.integration.handler;

import com.aaroen.spring.integration.persistence.entity.EmailEntity;
import com.aaroen.spring.integration.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

@Slf4j
@RequiredArgsConstructor
public class CheckoutEmailMessageHandler implements MessageHandler {
    private final EmailService emailService;

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        EmailEntity messagePayload = (EmailEntity) message.getPayload();
        log.info("The email {} has arrived to checkout email message handler.", messagePayload.getId());

        emailService.removeToQueue(messagePayload);
    }
}