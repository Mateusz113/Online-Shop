package com.mateusz113.product_service_model_public.dto;

import java.math.BigDecimal;
import java.util.List;

public record DetailedProductDto(
        Long id,
        String name,
        String brand,
        BigDecimal price,
        String type,
        int availableAmount,
        List<ProductDetailDto> details,
        List<CustomizationElementDto> customizations
) {
}
