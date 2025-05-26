package com.mateusz113.cart_service_model.exception;

import java.time.OffsetDateTime;

public class ProductServiceCommunicationErrorException extends WebException {
    public ProductServiceCommunicationErrorException(String errorMessage, OffsetDateTime errorTime) {
        super(errorMessage, 500, errorTime);
    }
}
