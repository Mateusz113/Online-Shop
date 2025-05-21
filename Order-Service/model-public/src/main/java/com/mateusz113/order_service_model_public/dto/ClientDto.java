package com.mateusz113.order_service_model_public.dto;

import lombok.Builder;

@Builder
public record ClientDto(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        AddressDto address
) {
}
