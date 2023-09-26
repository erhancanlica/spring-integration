package com.aaroen.spring.integration.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SendStatusEnum {
    CREATED(0, "The email has been created successfully and will be sent soon."),
    PENDING(1, "The email could not be sent, it will be sent again after a while."),
    FAILED(2, "Email was not sent, please try again after a certain period of time."),
    SUCCESSFUL(3, "Email was sent successfully.");

    private final int code;
    private final String message;
}