package com.mateusz113.product_service_model.exception;

import java.time.OffsetDateTime;

public class ProductFilterIllegalDataException extends WebException {
    public ProductFilterIllegalDataException(String message, OffsetDateTime errorTime) {
        super(message, 400, errorTime);
    }
}
