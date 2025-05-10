package com.mateusz113.cart_service_model.customization;

import com.mateusz113.cart_service_model.product.CustomizedProduct;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AppliedCustomization {
    private Long id;
    private Long sourceId;
    private String name;
    private Boolean multipleChoice;
    private CustomizedProduct product;
    private List<AppliedCustomizationOption> appliedOptions;

    public void fillWithSourceData(SourceCustomizationElement sourceCustomizationElement) {
        this.name = sourceCustomizationElement.getName();
        this.multipleChoice = sourceCustomizationElement.getMultipleChoice();
    }
}
