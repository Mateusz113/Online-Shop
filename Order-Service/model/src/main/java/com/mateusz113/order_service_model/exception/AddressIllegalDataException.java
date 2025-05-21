package com.mateusz113.order_service_model.exception;

import java.time.OffsetDateTime;

public class AddressIllegalDataException extends WebException {
    public AddressIllegalDataException(String message, OffsetDateTime errorTime) {
        super(message, 400, errorTime);
    }
}
