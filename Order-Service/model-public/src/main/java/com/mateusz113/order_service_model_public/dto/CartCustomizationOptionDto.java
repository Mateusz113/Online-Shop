package com.mateusz113.order_service_model_public.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CartCustomizationOptionDto(
        String name,
        BigDecimal priceDifference
) {
}
