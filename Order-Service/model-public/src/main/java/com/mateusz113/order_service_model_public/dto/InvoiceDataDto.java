package com.mateusz113.order_service_model_public.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record InvoiceDataDto(
        String from,
        String to,
        @JsonProperty("ship_to")
        String shipTo,
        String number,
        String currency,
        List<InvoiceItemDto> items,
        @JsonProperty("amount_paid")
        BigDecimal amountPaid
) {
}
