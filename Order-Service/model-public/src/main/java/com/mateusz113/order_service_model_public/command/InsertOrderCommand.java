package com.mateusz113.order_service_model_public.command;

public record InsertOrderCommand(
        Long cartId,
        Long clientId,
        String clientComment
) {
}
