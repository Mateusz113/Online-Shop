package com.mateusz113.product_service_model_public.dto;

import lombok.Builder;

@Builder
public record ProductDetailDto(
        Long id,
        String label,
        String description
) {
}
