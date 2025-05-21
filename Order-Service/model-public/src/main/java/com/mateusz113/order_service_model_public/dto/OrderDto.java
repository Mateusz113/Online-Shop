package com.mateusz113.order_service_model_public.dto;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record OrderDto(
        Long id,
        Long cartId,
        Long clientId,
        String clientComment,
        OffsetDateTime placementTime,
        String orderStatus,
        List<ProductDto> products
) {
}


