package com.mateusz113.cart_service_model_public.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record AppliedCustomizationDto(
        Long id,
        Long sourceId,
        String name,
        Boolean multipleChoice,
        List<AppliedCustomizationOptionDto> appliedOptions
) {
}
