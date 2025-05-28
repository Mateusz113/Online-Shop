package com.mateusz113.order_service_model_public.command;

import lombok.Builder;

@Builder
public record InsertOrderCommand(
        Long cartId,
        Long clientId,
        String clientComment
) {
}
