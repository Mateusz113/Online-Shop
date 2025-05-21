package com.mateusz113.order_service_model_public.command;

import com.mateusz113.order_service_model_public.dto.ClientDto;
import com.mateusz113.order_service_model_public.dto.FullOrderDto;
import lombok.Builder;

@Builder
public record ProcessOrderCommand(
        FullOrderDto fullOrderDto,
        String eventType,
        ClientDto clientDto
) {
}
