package com.mateusz113.product_service_model.customization;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CustomizationOption {
    private Long id;
    private String name;
    private boolean defaultOption;
    private BigDecimal priceDifference;
    private CustomizationElement customizationElement;
}
