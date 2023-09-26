package com.aaroen.spring.integration.controller;

import com.aaroen.spring.integration.model.request.EmailRequest;
import com.aaroen.spring.integration.model.response.EmailResponse;
import com.aaroen.spring.integration.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/email")
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/create")
    public EmailResponse create(@RequestBody EmailRequest emailRequest) {
        log.info("Email create endpoint handle request with sender id: {}", emailRequest.getSenderId());
        return emailService.create(emailRequest);
    }
}