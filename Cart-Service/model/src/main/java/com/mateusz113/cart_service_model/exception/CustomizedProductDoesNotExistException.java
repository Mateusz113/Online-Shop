package com.mateusz113.cart_service_model.exception;

import java.time.OffsetDateTime;

public class CustomizedProductDoesNotExistException extends WebException {
    public CustomizedProductDoesNotExistException(String errorMessage, OffsetDateTime errorTime) {
        super(errorMessage, 404, errorTime);
    }
}
