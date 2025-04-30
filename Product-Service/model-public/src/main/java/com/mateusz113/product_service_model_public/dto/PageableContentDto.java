package com.mateusz113.product_service_model_public.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PageableContentDto<T>(
        int totalPages,
        long totalElements,
        int pageNumber,
        int pageSize,
        List<T> elements
) {
}
