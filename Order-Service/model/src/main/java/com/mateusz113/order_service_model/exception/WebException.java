package com.mateusz113.order_service_model.exception;

import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public abstract class WebException extends RuntimeException {
    private final int statusCode;
    private final OffsetDateTime errorTime;

    public WebException(String message, int statusCode, OffsetDateTime errorTime) {
        super(message);
        this.statusCode = statusCode;
        this.errorTime = errorTime;
    }
}
