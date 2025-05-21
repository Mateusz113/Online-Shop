package com.mateusz113.order_service_model_public.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record CartProductDto(
        Long id,
        Long sourceId,
        String name,
        String brand,
        BigDecimal price,
        Integer quantity,
        List<CartCustomizationDto> appliedCustomizations
) {
}
