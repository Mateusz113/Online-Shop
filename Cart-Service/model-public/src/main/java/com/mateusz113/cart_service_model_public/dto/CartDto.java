package com.mateusz113.cart_service_model_public.dto;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record CartDto(
        Long id,
        OffsetDateTime creationDate,
        List<CustomizedProductDto> customizedProducts
) {
}
