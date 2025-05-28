package com.mateusz113.order_service_model.exception;

import java.time.OffsetDateTime;

public class OrderStatusUpdateErrorException extends WebException {
    public OrderStatusUpdateErrorException(String message, OffsetDateTime errorTime) {
        super(message, 400, errorTime);
    }
}
