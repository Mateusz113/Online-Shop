package com.mateusz113.order_service_model.exception;

import java.time.OffsetDateTime;

public class ProductInsufficientStockException extends WebException{
    public ProductInsufficientStockException(String message, OffsetDateTime errorTime) {
        super(message, 409, errorTime);
    }
}
