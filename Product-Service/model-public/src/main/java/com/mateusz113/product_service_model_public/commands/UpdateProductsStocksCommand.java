package com.mateusz113.product_service_model_public.commands;

import lombok.Builder;

import java.util.Map;

@Builder
public record UpdateProductsStocksCommand(
        Map<Long, Integer> productsStocksMap
) {
}
