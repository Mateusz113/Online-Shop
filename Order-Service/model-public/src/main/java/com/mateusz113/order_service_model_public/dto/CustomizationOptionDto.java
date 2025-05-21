package com.mateusz113.order_service_model_public.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CustomizationOptionDto(
        Long id,
        String name,
        BigDecimal priceDifference
) {
}
