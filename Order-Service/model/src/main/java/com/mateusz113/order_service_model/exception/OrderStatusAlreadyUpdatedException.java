package com.mateusz113.order_service_model.exception;

import java.time.OffsetDateTime;

public class OrderStatusAlreadyUpdatedException extends WebException {
    public OrderStatusAlreadyUpdatedException(String message, OffsetDateTime errorTime) {
        super(message, 400, errorTime);
    }
}
