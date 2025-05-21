package com.mateusz113.order_service_model_public.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CustomizationDto(
        Long id,
        String name,
        Boolean multipleChoice,
        List<CustomizationOptionDto> options
) {
}
