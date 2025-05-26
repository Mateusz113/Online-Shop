package com.mateusz113.cart_service_model_public.command;

import lombok.Builder;

import java.util.List;

@Builder
public record InsertAppliedCustomizationCommand(
        Long sourceId,
        List<InsertAppliedCustomizationOptionCommand> appliedOptions
) {
}
