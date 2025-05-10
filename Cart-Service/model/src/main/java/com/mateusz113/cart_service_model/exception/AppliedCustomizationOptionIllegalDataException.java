package com.mateusz113.cart_service_model.exception;

import java.time.OffsetDateTime;

public class AppliedCustomizationOptionIllegalDataException extends WebException {
    public AppliedCustomizationOptionIllegalDataException(String errorMessage, OffsetDateTime errorTime) {
        super(errorMessage, 400, errorTime);
    }
}
