package com.mateusz113.order_service_model.exception;

import java.time.OffsetDateTime;

public class ProductNotInStockException extends WebException {
    public ProductNotInStockException(String message, OffsetDateTime errorTime) {
        super(message, 404, errorTime);
    }
}
