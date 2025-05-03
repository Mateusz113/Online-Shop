package com.mateusz113.product_service_model_public.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CustomizationElementDto(
        Long id,
        String name,
        Boolean multipleChoice,
        List<CustomizationOptionDto> options
) {
}
