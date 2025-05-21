package com.mateusz113.order_service_model.exception;

import java.time.OffsetDateTime;

public class OrderIllegalDataException extends WebException {
    public OrderIllegalDataException(String message, OffsetDateTime errorTime) {
        super(message, 400, errorTime);
    }
}
