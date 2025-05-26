package com.mateusz113.cart_service_model_public.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record CustomizedProductDto(
        Long id,
        Long sourceId,
        String name,
        String brand,
        BigDecimal price,
        Integer quantity,
        List<AppliedCustomizationDto> appliedCustomizations
) {
}
