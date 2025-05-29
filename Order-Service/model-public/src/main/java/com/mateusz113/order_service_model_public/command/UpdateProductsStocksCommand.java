package com.mateusz113.order_service_model_public.command;

import lombok.Builder;

import java.util.Map;

@Builder
public record UpdateProductsStocksCommand(
        Map<Long, Integer> productsStocksMap
) {
}
