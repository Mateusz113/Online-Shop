package com.mateusz113.product_service_model.product;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDetail {
    private Long id;
    private String label;
    private String description;
    private Product product;
}
