package com.mateusz113.cart_service_model.exception;

import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public abstract class WebException extends RuntimeException {
    private final Integer statusCode;
    private final OffsetDateTime errorTime;

    public WebException(String errorMessage, Integer statusCode, OffsetDateTime errorTime) {
        super(errorMessage);
        this.statusCode = statusCode;
        this.errorTime = errorTime;
    }
}
