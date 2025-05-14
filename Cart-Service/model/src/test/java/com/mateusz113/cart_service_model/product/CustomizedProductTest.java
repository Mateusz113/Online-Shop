package com.mateusz113.cart_service_model.product;

import com.mateusz113.cart_service_model.customization.AppliedCustomization;
import com.mateusz113.cart_service_model.customization.AppliedCustomizationOption;
import com.mateusz113.cart_service_model.customization.SourceCustomizationElement;
import com.mateusz113.cart_service_model.customization.SourceCustomizationOption;
import org.junit.jupiter.api.Test;

import static com.mateusz113.cart_service_model.util.CartServiceModelTestUtil.getCustomizedProduct;
import static com.mateusz113.cart_service_model.util.CartServiceModelTestUtil.getSourceProduct;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomizedProductTest {
    @Test
    void fillWithSourceData_UpdatesDataWithSourceValues() {
        CustomizedProduct customizedProduct = getCustomizedProduct();
        SourceProduct sourceProduct = getSourceProduct();

        customizedProduct.fillWithSourceData(sourceProduct);

        assertEquals(sourceProduct.getName(), customizedProduct.getName());
        assertEquals(sourceProduct.getBrand(), customizedProduct.getBrand());
        assertEquals(sourceProduct.getPrice(), customizedProduct.getPrice());
        for (AppliedCustomization appliedCustomization: customizedProduct.getAppliedCustomizations()) {
            SourceCustomizationElement sourceCustomization = sourceProduct.getCustomizations().stream()
                    .filter(sourceCustomizationElement -> sourceCustomizationElement.getId().equals(appliedCustomization.getSourceId()))
                    .findFirst()
                    .orElseThrow(IllegalStateException::new);
            assertEquals(sourceCustomization.getName(), appliedCustomization.getName());
            assertEquals(sourceCustomization.getMultipleChoice(), appliedCustomization.getMultipleChoice());
            for (AppliedCustomizationOption appliedOption : appliedCustomization.getAppliedOptions()) {
                SourceCustomizationOption sourceOption = sourceCustomization.getOptions().stream()
                        .filter(customizationOption -> customizationOption.getId().equals(appliedOption.getSourceId()))
                        .findFirst()
                        .orElseThrow(IllegalStateException::new);
                assertEquals(sourceOption.getName(), appliedOption.getName());
                assertEquals(sourceOption.getPriceDifference(), appliedOption.getPriceDifference());
            }
        }
    }
}
