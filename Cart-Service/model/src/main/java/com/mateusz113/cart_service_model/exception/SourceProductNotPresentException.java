package com.mateusz113.cart_service_model.exception;

import java.time.OffsetDateTime;

public class SourceProductNotPresentException extends WebException {
    public SourceProductNotPresentException(String errorMessage, OffsetDateTime errorTime) {
        super(errorMessage, 404, errorTime);
    }
}
