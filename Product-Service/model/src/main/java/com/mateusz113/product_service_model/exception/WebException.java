package com.mateusz113.product_service_model.exception;

import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public abstract class WebException extends RuntimeException {
    private final Integer statusCode;
    private final OffsetDateTime errorTime;

    public WebException(String message, Integer statusCode, OffsetDateTime errorTime) {
        super(message);
        this.statusCode = statusCode;
        this.errorTime = errorTime;
    }
}
