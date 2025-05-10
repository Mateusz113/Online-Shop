package com.mateusz113.cart_service_model_public.error;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record ErrorMessage(
        String message,
        int statusCode,
        OffsetDateTime errorTime
) {
}
