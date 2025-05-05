package com.mateusz113.cart_service_model_public.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AppliedCustomizationOptionDto(
        String name,
        BigDecimal priceDifference
) {
}
