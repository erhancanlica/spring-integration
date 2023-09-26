package com.aaroen.spring.integration.service;

import com.aaroen.spring.integration.model.request.EmailRequest;
import com.aaroen.spring.integration.model.response.EmailResponse;
import com.aaroen.spring.integration.persistence.entity.EmailEntity;

public interface EmailService {
    EmailResponse create(EmailRequest emailRequest);
    String send(EmailEntity emailEntity);
    void addToQueue(EmailEntity emailEntity);
    void removeToQueue(EmailEntity emailEntity);
}