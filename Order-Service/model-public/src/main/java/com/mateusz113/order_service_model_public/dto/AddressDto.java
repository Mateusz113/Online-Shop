package com.mateusz113.order_service_model_public.dto;

import lombok.Builder;

@Builder
public record AddressDto(
        String country,
        String city,
        String zipCode,
        String street,
        String buildingNumber,
        String apartmentNumber
) {
}
