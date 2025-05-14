package com.mateusz113.cart_service_model.product;

import com.mateusz113.cart_service_model.cart.Cart;
import com.mateusz113.cart_service_model.customization.AppliedCustomization;
import com.mateusz113.cart_service_model.customization.SourceCustomizationElement;
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
    private Cart cart;
    private List<AppliedCustomization> appliedCustomizations;

    public void fillWithSourceData(SourceProduct sourceProduct) {
        this.name = sourceProduct.getName();
        this.brand = sourceProduct.getBrand();
        this.price = sourceProduct.getPrice();
        for (AppliedCustomization appliedCustomization : appliedCustomizations) {
            SourceCustomizationElement sourceCustomization = sourceProduct.getCustomizations().stream()
                    .filter(sourceCustomizationElement -> sourceCustomizationElement.getId().equals(appliedCustomization.getSourceId()))
                    .findFirst()
                    .orElseThrow(IllegalStateException::new);
            appliedCustomization.fillWithSourceData(sourceCustomization);
        }
    }
}
