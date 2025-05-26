package com.mateusz113.cart_service_model.customization;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SourceCustomizationOption {
    private Long id;
    private String name;
    private BigDecimal priceDifference;
}
