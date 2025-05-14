package com.mateusz113.cart_service_model.exception;

import java.time.OffsetDateTime;

public class ProductServiceIsUnavailableException extends WebException {
    public ProductServiceIsUnavailableException(String errorMessage, OffsetDateTime errorTime) {
        super(errorMessage, 503, errorTime);
    }
}
