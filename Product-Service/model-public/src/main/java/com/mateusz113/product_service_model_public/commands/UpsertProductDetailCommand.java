package com.mateusz113.product_service_model_public.commands;

import lombok.Builder;

@Builder
public record UpsertProductDetailCommand(
        String label,
        String value
) {
}
