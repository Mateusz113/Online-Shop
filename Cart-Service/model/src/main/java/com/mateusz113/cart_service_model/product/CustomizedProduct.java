package com.mateusz113.cart_service_model.product;

import com.mateusz113.cart_service_model.customization.AppliedCustomization;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CustomizedProduct {
    private Long id;
    private Long sourceId;
    private String name;
    private String brand;
    private BigDecimal price;
    private Integer quantity;
    private List<AppliedCustomization> appliedCustomizations;
}
