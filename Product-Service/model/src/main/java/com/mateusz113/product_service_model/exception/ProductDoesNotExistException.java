package com.mateusz113.product_service_model.exception;

import java.time.OffsetDateTime;

public class ProductDoesNotExistException extends WebException {
    public ProductDoesNotExistException(String message, OffsetDateTime errorTime) {
        super(message, 404, errorTime);
    }
}
