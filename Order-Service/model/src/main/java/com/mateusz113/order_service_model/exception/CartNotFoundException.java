package com.mateusz113.order_service_model.exception;

import java.time.OffsetDateTime;

public class CartNotFoundException extends WebException {
    public CartNotFoundException(String message, OffsetDateTime errorTime) {
        super(message, 404, errorTime);
    }
}
