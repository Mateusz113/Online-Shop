package com.mateusz113.cart_service_model.product;

import com.mateusz113.cart_service_model.customization.SourceCustomizationElement;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class SourceProduct {
    private Long id;
    private String name;
    private String brand;
    private BigDecimal price;
    private Integer availableAmount;
    private List<SourceCustomizationElement> customizations;
}
