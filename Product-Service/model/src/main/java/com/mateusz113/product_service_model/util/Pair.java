package com.mateusz113.product_service_model.util;

import lombok.Builder;

@Builder
public record Pair<T, R>(
        T first,
        R second
) {
}
