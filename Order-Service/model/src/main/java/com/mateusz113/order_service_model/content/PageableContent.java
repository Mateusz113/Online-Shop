package com.mateusz113.order_service_model.content;

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
