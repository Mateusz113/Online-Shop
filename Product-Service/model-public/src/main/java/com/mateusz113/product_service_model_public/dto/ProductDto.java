package com.mateusz113.product_service_model_public.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ProductDto(
        Long id,
        String name,
        String brand,
        BigDecimal price,
        String type,
        Integer availableAmount,
        List<CustomizationElementDto> customizations
) {
}
