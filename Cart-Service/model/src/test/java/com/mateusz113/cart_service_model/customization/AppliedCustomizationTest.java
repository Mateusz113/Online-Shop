package com.mateusz113.cart_service_model.customization;

import org.junit.jupiter.api.Test;

import static com.mateusz113.cart_service_model.util.CartServiceModelTestUtil.getAppliedCustomization;
import static com.mateusz113.cart_service_model.util.CartServiceModelTestUtil.getSourceCustomizationElement;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppliedCustomizationTest {
    @Test
    void fillWithSourceData_UpdatesDataWithSourceValues() {
        AppliedCustomization appliedCustomization = getAppliedCustomization();
        SourceCustomizationElement sourceCustomization = getSourceCustomizationElement();

        appliedCustomization.fillWithSourceData(sourceCustomization);

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
