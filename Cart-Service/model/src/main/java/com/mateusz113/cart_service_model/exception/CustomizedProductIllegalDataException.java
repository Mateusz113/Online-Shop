package com.mateusz113.cart_service_model.exception;

import java.time.OffsetDateTime;

public class CustomizedProductIllegalDataException extends WebException {
    public CustomizedProductIllegalDataException(String errorMessage, OffsetDateTime errorTime) {
        super(errorMessage, 400, errorTime);
    }
}
