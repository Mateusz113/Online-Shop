package com.mateusz113.order_service_model.invoice;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class InvoiceData {
    private String from;
    private String to;
    private String shipTo;
    private String number;
    @Builder.Default
    private String currency = "PLN";
    @Builder.Default
    private List<InvoiceItem> items = new ArrayList<>();
    @Builder.Default
    private BigDecimal amountPaid = BigDecimal.ZERO;
}
