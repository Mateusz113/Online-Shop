package com.mateusz113.product_service_model.filter;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductFilter(
        String name,
        String brand,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String type,
        Integer minAvailableAmount
) {
}
