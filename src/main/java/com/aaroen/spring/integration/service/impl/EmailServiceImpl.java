package com.aaroen.spring.integration.service.impl;

import com.aaroen.spring.integration.integration.gateway.EmailDeliveryGateway;
import com.aaroen.spring.integration.integration.gateway.EmailNotificationGateway;
import com.aaroen.spring.integration.mapper.EmailMapper;
import com.aaroen.spring.integration.model.enums.SendStatusEnum;
import com.aaroen.spring.integration.model.request.EmailRequest;
import com.aaroen.spring.integration.model.response.EmailResponse;
import com.aaroen.spring.integration.model.response.Result;
import com.aaroen.spring.integration.persistence.entity.EmailEntity;
import com.aaroen.spring.integration.persistence.repository.EmailRepository;
import com.aaroen.spring.integration.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final EmailMapper emailMapper;
    private final JavaMailSender javaMailSender;
    private final EmailRepository emailRepository;
    private final EmailDeliveryGateway emailDeliveryGateway;
    private final EmailNotificationGateway emailNotificationGateway;

    private final Object lockObject = new Object();
    private final Set<Long> uniqueEmailIds = new HashSet<>();

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public EmailResponse create(EmailRequest emailRequest) {
        EmailResponse emailResponse = new EmailResponse();
        try {
            EmailEntity emailEntity = emailMapper.mapToEmailEntity(emailRequest);
            emailEntity.setSendStatus(SendStatusEnum.PENDING);
            emailRepository.save(emailEntity);

            String createdMessage = SendStatusEnum.CREATED.getMessage();
            log.info(createdMessage + " Email id: {}", emailEntity.getId());

            Result result = resultBuilder(SendStatusEnum.CREATED.getCode(), createdMessage);
            emailResponse.setResult(result);

        } catch (Exception e) {
            String failedMessage = SendStatusEnum.FAILED.getMessage();
            log.info(failedMessage + " Sender id: {}", emailRequest.getSenderId());

            Result result = resultBuilder(SendStatusEnum.FAILED.getCode(), failedMessage);
            emailResponse.setResult(result);
        }

        return emailResponse;
    }

    @Override
    public String send(EmailEntity emailEntity) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = prepareMimeMessageHelper(emailEntity, mimeMessage);
            addAttachment(emailEntity.getAttachment(), mimeMessageHelper);
            javaMailSender.send(mimeMessage);

            emailEntity.setSendDate(LocalDateTime.now());
            emailEntity.setSendStatus(SendStatusEnum.SUCCESSFUL);
            emailRepository.save(emailEntity);

            String successfulMessage = SendStatusEnum.SUCCESSFUL.getMessage();
            log.info(successfulMessage + " Email id: {}", emailEntity.getId());
            return successfulMessage;
        } catch (Exception e) {
            return handleMailSendingError(emailEntity);
        }
    }

    @Override
    public void addToQueue(EmailEntity emailEntity) {
        synchronized (lockObject) {
            if (uniqueEmailIds.add(emailEntity.getId())) {
                log.info("Added email to list with id : {}", emailEntity.getId());
                emailDeliveryGateway.process(emailEntity);
            }
        }
    }

    @Override
    public void removeToQueue(EmailEntity emailEntity) {
        synchronized (lockObject) {
            log.info("Removed email to list with id : {}", emailEntity.getId());
            uniqueEmailIds.remove(emailEntity.getId());
            emailNotificationGateway.process(emailEntity);
        }
    }

    private Result resultBuilder(int number, String success) {
        return Result.builder()
                .resultCode(number)
                .resultMessage(success)
                .build();
    }

    private MimeMessageHelper prepareMimeMessageHelper(EmailEntity emailRequest, MimeMessage mimeMessage) throws MessagingException {
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom(sender);
        mimeMessageHelper.setTo(emailRequest.getRecipientEmail());
        mimeMessageHelper.setText(emailRequest.getMessage());
        mimeMessageHelper.setSubject(emailRequest.getSubject());
        return mimeMessageHelper;
    }

    private void addAttachment(String attachment, MimeMessageHelper mimeMessageHelper) throws MessagingException {
        if (attachment != null) {
            FileSystemResource fileSystemResource = new FileSystemResource(new File(attachment));
            mimeMessageHelper.addAttachment(Objects.requireNonNull(fileSystemResource.getFilename()), fileSystemResource);
        }
    }

    private String handleMailSendingError(EmailEntity emailEntity) {
        StringBuilder messageBuilder = new StringBuilder();

        if (emailEntity.getRetryCount() != null && emailEntity.getRetryCount() > 3) {
            emailEntity.setSendStatus(SendStatusEnum.FAILED);

            String failedMessage = SendStatusEnum.FAILED.getMessage();
            log.info(failedMessage + " Email id: {}", emailEntity.getId());

            messageBuilder.append(failedMessage);
        } else {
            emailEntity.setRetryCount(getRetryCount(emailEntity));
            emailEntity.setSendStatus(SendStatusEnum.PENDING);

            String pendingMessage = SendStatusEnum.PENDING.getMessage();
            log.info(pendingMessage + " Email id: {}", emailEntity.getId());

            messageBuilder.append(pendingMessage);
        }

        emailRepository.save(emailEntity);
        return messageBuilder.toString();
    }


    private int getRetryCount(EmailEntity emailEntity) {
        return emailEntity.getRetryCount() == null ? 1 : emailEntity.getRetryCount() + 1;
    }
}