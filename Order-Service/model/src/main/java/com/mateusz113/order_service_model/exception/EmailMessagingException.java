package com.mateusz113.order_service_model.exception;

import java.time.OffsetDateTime;

public class EmailMessagingException extends WebException {
    public EmailMessagingException(String message, OffsetDateTime errorTime) {
        super(message, 500, errorTime);
    }
}
