package com.mateusz113.product_service_model_public.commands;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record UpsertProductCommand(
        String name,
        String brand,
        BigDecimal price,
        String type,
        int availableAmount,
        List<UpsertCustomizationElementCommand> customizationElements
) {
}
