package com.mateusz113.cart_service_model.exception;

import java.time.OffsetDateTime;

public class CartDoesNotExistException extends WebException {
    public CartDoesNotExistException(String errorMessage, OffsetDateTime errorTime) {
        super(errorMessage, 404, errorTime);
    }
}
