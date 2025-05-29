package com.mateusz113.product_service_model_public.commands;

import lombok.Builder;

import java.util.List;

@Builder
public record UpsertCustomizationElementCommand(
        String name,
        Boolean multipleChoice,
        List<UpsertCustomizationOptionCommand> options
) {
}
