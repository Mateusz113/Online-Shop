package com.mateusz113.product_service_model_public.commands;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record FilterProductCommand(
        String name,
        String brand,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String type,
        Integer minAvailableAmount
) {
}
