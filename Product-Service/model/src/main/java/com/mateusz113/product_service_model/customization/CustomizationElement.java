package com.mateusz113.product_service_model.customization;

import com.mateusz113.product_service_model.product.Product;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CustomizationElement {
    private Long id;
    private String name;
    private Boolean multipleChoice;
    private Product product;
    private List<CustomizationOption> options;
}
