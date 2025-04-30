package com.mateusz113.product_service_model.exception;

import java.time.OffsetDateTime;

public class ProductIllegalDataException extends WebException {
    public ProductIllegalDataException(String message, OffsetDateTime errorTime) {
        super(message, 400, errorTime);
    }
}
