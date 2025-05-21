package com.mateusz113.order_service_model.exception;

import java.time.OffsetDateTime;

public class ClientIllegalDataException extends WebException {
    public ClientIllegalDataException(String message, OffsetDateTime errorTime) {
        super(message, 400, errorTime);
    }
}
