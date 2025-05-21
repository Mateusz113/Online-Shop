package com.mateusz113.order_service_model.exception;

import java.time.OffsetDateTime;

public class OrderNotPaidException extends WebException {
    public OrderNotPaidException(String message, OffsetDateTime errorTime) {
        super(message, 400, errorTime);
    }
}
