package com.mateusz113.product_service_model.exception;

import java.time.OffsetDateTime;

public class ProductDetailIllegalDataException extends WebException {
    public ProductDetailIllegalDataException(String message, OffsetDateTime errorTime) {
        super(message, 400, errorTime);
    }
}
