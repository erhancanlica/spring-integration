package com.aaroen.spring.integration.mapper;

import com.aaroen.spring.integration.model.request.EmailRequest;
import com.aaroen.spring.integration.persistence.entity.EmailEntity;
import com.aaroen.spring.integration.persistence.entity.EmailEntity.EmailEntityBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-09-26T02:02:28+0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 21 (Oracle Corporation)"
)
@Component
public class EmailMapperImpl implements EmailMapper {

    @Override
    public EmailEntity mapToEmailEntity(EmailRequest emailRequest) {
        if ( emailRequest == null ) {
            return null;
        }

        EmailEntityBuilder emailEntity = EmailEntity.builder();

        emailEntity.senderId( emailRequest.getSenderId() );
        emailEntity.senderName( emailRequest.getSenderName() );
        emailEntity.subject( emailRequest.getSubject() );
        emailEntity.message( emailRequest.getMessage() );
        emailEntity.attachment( emailRequest.getAttachment() );
        emailEntity.recipientEmail( emailRequest.getRecipientEmail() );

        return emailEntity.build();
    }
}
