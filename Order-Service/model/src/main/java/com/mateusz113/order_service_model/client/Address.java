package com.mateusz113.order_service_model.client;

import lombok.Builder;

@Builder
public record Address(
        String country,
        String city,
        String zipCode,
        String street,
        String buildingNumber,
        String apartmentNumber
) {
}
