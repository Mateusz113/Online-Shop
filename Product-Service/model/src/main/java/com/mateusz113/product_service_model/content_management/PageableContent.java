package com.mateusz113.product_service_model.content_management;

import lombok.Builder;

import java.util.List;

@Builder
public record PageableContent<T>(
        Integer totalPages,
        Long totalElements,
        Integer pageNumber,
        Integer pageSize,
        List<T> elements
) {
}
