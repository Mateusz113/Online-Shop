package com.mateusz113.cart_service_model.customization;

import org.junit.jupiter.api.Test;

import static com.mateusz113.cart_service_model.util.CartServiceModelTestUtil.getAppliedCustomizationOption;
import static com.mateusz113.cart_service_model.util.CartServiceModelTestUtil.getSourceCustomizationOption;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppliedCustomizationOptionTest {
    @Test
    void fillWithSourceData_UpdatesDataWithSourceValues() {
        AppliedCustomizationOption appliedOption = getAppliedCustomizationOption();
        SourceCustomizationOption sourceOption = getSourceCustomizationOption();

        appliedOption.fillWithSourceData(sourceOption);

        assertEquals(sourceOption.getName(), appliedOption.getName());
        assertEquals(sourceOption.getPriceDifference(), appliedOption.getPriceDifference());
    }
}
