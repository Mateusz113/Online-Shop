package com.mateusz113.product_service_model_public.error;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record ErrorMessage(
        String message,
        Integer statusCode,
        OffsetDateTime errorTime
) {
}
