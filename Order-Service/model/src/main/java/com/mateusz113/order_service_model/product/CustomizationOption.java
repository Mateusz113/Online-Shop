package com.mateusz113.order_service_model.product;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CustomizationOption {
    private Long id;
    private String name;
    private BigDecimal priceDifference;
}
