package com.mateusz113.order_service_model_public.dto;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record FullOrderDto(
        Long id,
        Long cartId,
        Long clientId,
        String clientComment,
        OffsetDateTime placementTime,
        String orderStatus,
        byte[] invoice,
        List<ProductDto> products
) {
}
