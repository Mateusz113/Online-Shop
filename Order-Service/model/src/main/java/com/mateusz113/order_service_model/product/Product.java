package com.mateusz113.order_service_model.product;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class Product {
    private Long id;
    private Long sourceId;
    private String name;
    private String brand;
    private BigDecimal price;
    private Integer quantity;
    private List<Customization> customizations;
}
