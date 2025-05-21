package com.mateusz113.product_service_model.exception;

import java.time.OffsetDateTime;

public class InvalidProductStockException extends WebException {
    public InvalidProductStockException(String message, OffsetDateTime errorTime) {
        super(message, 400, errorTime);
    }
}
