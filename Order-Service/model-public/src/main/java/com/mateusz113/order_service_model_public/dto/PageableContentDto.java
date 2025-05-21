package com.mateusz113.order_service_model_public.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PageableContentDto<T>(
        Integer totalPages,
        Long totalElements,
        Integer pageNumber,
        Integer pageSize,
        List<T> elements
) {
}
