package com.mateusz113.product_service_model.exception;

import java.time.OffsetDateTime;

public class CustomizationElementIllegalDataException extends WebException {
    public CustomizationElementIllegalDataException(String message, OffsetDateTime errorTime) {
        super(message, 400, errorTime);
    }
}
