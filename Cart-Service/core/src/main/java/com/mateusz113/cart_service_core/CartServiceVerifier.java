package com.mateusz113.cart_service_core;

import com.mateusz113.cart_service_model.customization.AppliedCustomization;
import com.mateusz113.cart_service_model.customization.AppliedCustomizationOption;
import com.mateusz113.cart_service_model.customization.SourceCustomizationElement;
import com.mateusz113.cart_service_model.exception.AppliedCustomizationIllegalDataException;
import com.mateusz113.cart_service_model.exception.AppliedCustomizationOptionIllegalDataException;
import com.mateusz113.cart_service_model.exception.CustomizedProductIllegalDataException;
import com.mateusz113.cart_service_model.product.CustomizedProduct;
import com.mateusz113.cart_service_model.product.SourceProduct;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.OffsetDateTime;

@RequiredArgsConstructor
public class CartServiceVerifier {
    private final Clock clock;

    public void verifyCustomizedProductAndCompareToSource(SourceProduct sourceProduct, CustomizedProduct customizedProduct) {
        if (customizedProduct.getQuantity() <= 0) {
            throw new CustomizedProductIllegalDataException("Quantity of product in cart has to be positive.", OffsetDateTime.now(clock));
        }
        if (sourceProduct.getAvailableAmount() < customizedProduct.getQuantity()) {
            throw new CustomizedProductIllegalDataException("Cart product quantity has to be less of equal to the available amount.", OffsetDateTime.now(clock));
        }
        for (AppliedCustomization appliedCustomization : customizedProduct.getAppliedCustomizations()) {
            SourceCustomizationElement sourceCustomizationElement = sourceProduct.getCustomizations().stream()
                    .filter(customizationElement -> customizationElement.getId().equals(appliedCustomization.getSourceId()))
                    .findFirst()
                    .orElseThrow(() -> new CustomizedProductIllegalDataException("Customizations of sent products do not match source data.", OffsetDateTime.now(clock)));
            if (!sourceCustomizationElement.getMultipleChoice() && appliedCustomization.getAppliedOptions().size() != 1) {
                throw new AppliedCustomizationIllegalDataException(
                        "There has to be exact one customization option selected for non multiple choice customizations.",
                        OffsetDateTime.now(clock)
                );
            }
            for (AppliedCustomizationOption appliedOption : appliedCustomization.getAppliedOptions()) {
                if (sourceCustomizationElement.getOptions().stream()
                        .noneMatch(customizationOption -> customizationOption.getId().equals(appliedOption.getSourceId()))
                ) {
                    throw new AppliedCustomizationOptionIllegalDataException("Customizations options of sent products do not match source data.", OffsetDateTime.now(clock));
                }
            }
        }
    }
}
