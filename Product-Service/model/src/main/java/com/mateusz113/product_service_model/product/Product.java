package com.mateusz113.product_service_model.product;

import com.mateusz113.product_service_model.customization.CustomizationElement;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class Product {
    private Long id;
    private String name;
    private String brand;
    private BigDecimal price;
    private String type;
    private int availableAmount;
    private List<CustomizationElement> customizations;

    public void update(Product newData) {
        this.name = newData.name;
        this.brand = newData.brand;
        this.price = newData.price;
        this.type = newData.type;
        this.availableAmount = newData.availableAmount;
        this.customizations = newData.customizations;
    }
}
