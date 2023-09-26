package com.aaroen.spring.integration.mapper;

import com.aaroen.spring.integration.model.request.EmailRequest;
import com.aaroen.spring.integration.persistence.entity.EmailEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmailMapper {
    EmailEntity mapToEmailEntity(EmailRequest emailRequest);
}