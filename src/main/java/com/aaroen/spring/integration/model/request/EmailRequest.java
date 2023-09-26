package com.aaroen.spring.integration.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {
    @NotNull
    private Long senderId;
    @NotBlank
    private String senderName;
    @NotBlank
    private String subject;
    @NotBlank
    private String message;
    @NotBlank
    private String recipientEmail;
    private String attachment;
}