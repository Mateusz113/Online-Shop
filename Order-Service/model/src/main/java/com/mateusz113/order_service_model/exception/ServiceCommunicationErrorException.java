package com.mateusz113.order_service_model.exception;

import java.time.OffsetDateTime;

public class ServiceCommunicationErrorException extends WebException {
    public ServiceCommunicationErrorException(String message, OffsetDateTime errorTime) {
        super(message, 500, errorTime);
    }
}
