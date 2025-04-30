package com.mateusz113.product_service_model.content_management;

import lombok.Builder;

import java.util.List;

@Builder
public record PageableContent<T>(
        int totalPages,
        long totalElements,
        int pageNumber,
        int pageSize,
        List<T> elements
) {
}
