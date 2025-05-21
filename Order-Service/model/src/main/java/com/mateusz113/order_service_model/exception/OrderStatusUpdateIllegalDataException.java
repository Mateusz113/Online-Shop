package com.mateusz113.order_service_model.exception;

import java.time.OffsetDateTime;

public class OrderStatusUpdateIllegalDataException extends WebException {
    public OrderStatusUpdateIllegalDataException(String message, OffsetDateTime errorTime) {
        super(message, 400, errorTime);
    }
}
