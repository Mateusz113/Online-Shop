package com.mateusz113.cart_service_model_public.command;

import lombok.Builder;

@Builder
public record InsertAppliedCustomizationOptionCommand(
        Long sourceId
) {
}
