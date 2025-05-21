package com.mateusz113.order_service_model_public.command;

import com.mateusz113.order_service_model_public.dto.ClientDto;

public record UpdateOrderStatusCommand(
        Long orderId,
        ClientDto clientDto
) {
}
