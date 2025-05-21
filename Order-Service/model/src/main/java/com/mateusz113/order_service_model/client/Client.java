package com.mateusz113.order_service_model.client;

import lombok.Builder;

@Builder
public record Client(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        Address address
) {
}
