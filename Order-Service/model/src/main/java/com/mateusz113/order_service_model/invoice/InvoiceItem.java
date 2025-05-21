package com.mateusz113.order_service_model.invoice;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record InvoiceItem(
        String name,
        Integer quantity,
        BigDecimal price,
        String description
) {
}
