package com.mateusz113.product_service_model_public.commands;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UpsertCustomizationOptionCommand(
        String name,
        Boolean defaultOption,
        BigDecimal priceDifference
) {
}
