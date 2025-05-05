package com.mateusz113.cart_service_model.customization;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AppliedCustomizationOption {
    private Long id;
    private Long sourceId;
    private String name;
    private BigDecimal priceDifference;
}
