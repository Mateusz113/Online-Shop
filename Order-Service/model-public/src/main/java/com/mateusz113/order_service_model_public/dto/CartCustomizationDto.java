package com.mateusz113.order_service_model_public.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CartCustomizationDto(
        String name,
        Boolean multipleChoice,
        List<CartCustomizationOptionDto> appliedOptions
) {
}
