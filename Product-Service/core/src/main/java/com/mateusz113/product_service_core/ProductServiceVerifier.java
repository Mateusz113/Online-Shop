package com.mateusz113.product_service_core;

import com.mateusz113.product_service_model.customization.CustomizationElement;
import com.mateusz113.product_service_model.customization.CustomizationOption;
import com.mateusz113.product_service_model.exception.CustomizationElementIllegalDataException;
import com.mateusz113.product_service_model.exception.CustomizationOptionIllegalDataException;
import com.mateusz113.product_service_model.exception.ProductIllegalDataException;
import com.mateusz113.product_service_model.product.Product;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class ProductServiceVerifier {
    private final Clock clock;

    public void verifyProduct(Product product) {
        verifyNonNullFields(product);
        verifyNonNullCustomizationsFields(product.getCustomizations());
        verifyPrice(product.getPrice());
        verifyAvailableAmount(product.getAvailableAmount());
        verifyCustomizations(product.getCustomizations());
    }

    private void verifyNonNullFields(Product product) {
        if (Objects.isNull(product.getName())
                || Objects.isNull(product.getBrand())
                || Objects.isNull(product.getPrice())
                || Objects.isNull(product.getType())
                || Objects.isNull(product.getCustomizations())
        ) {
            throw new ProductIllegalDataException("There cannot be null fields in product!", OffsetDateTime.now(clock));
        }
    }

    private void verifyNonNullCustomizationsFields(List<CustomizationElement> customizations) {
        for (CustomizationElement customization : customizations) {
            if (Objects.isNull(customization.getName())
                    || Objects.isNull(customization.getProduct())
                    || Objects.isNull(customization.getOptions())) {
                throw new CustomizationElementIllegalDataException("There cannot be null fields in customization element.", OffsetDateTime.now(clock));
            }
            verifyNonNullCustomizationOptionsFields(customization.getOptions());
        }
    }

    private void verifyNonNullCustomizationOptionsFields(List<CustomizationOption> options) {
        for (CustomizationOption option : options) {
            if (Objects.isNull(option.getName())
                    || Objects.isNull(option.getPriceDifference())
                    || Objects.isNull(option.getCustomizationElement())) {
                throw new CustomizationOptionIllegalDataException("There cannot be null fields in customization options.", OffsetDateTime.now(clock));
            }
        }
    }

    private void verifyPrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.valueOf(0)) < 0) {
            throw new ProductIllegalDataException("Product price has to be positive.", OffsetDateTime.now(clock));
        }
        if (price.stripTrailingZeros().scale() > 2) {
            throw new ProductIllegalDataException("Product price has a maximum of 2 digits after decimal point.", OffsetDateTime.now(clock));
        }
    }

    private void verifyAvailableAmount(Integer availableAmount) {
        if (availableAmount < 0) {
            throw new ProductIllegalDataException("Product has to have a non-negative available amount.", OffsetDateTime.now(clock));
        }
    }

    private void verifyCustomizations(List<CustomizationElement> customizations) {
        for (CustomizationElement customization : customizations) {
            long defaultOptionsCount = customization.getOptions().stream()
                    .filter(CustomizationOption::isDefaultOption)
                    .count();
            if (defaultOptionsCount > 1) {
                throw new CustomizationElementIllegalDataException("There cannot be more than one default option for each customization.", OffsetDateTime.now(clock));
            }
            if (!customization.isMultipleChoice() && defaultOptionsCount == 0) {
                throw new CustomizationElementIllegalDataException("There has to be a default option for single choice customizations.", OffsetDateTime.now(clock));
            }
            for (CustomizationOption option : customization.getOptions()) {
                if (option.getPriceDifference().compareTo(BigDecimal.valueOf(0)) < 0) {
                    throw new CustomizationOptionIllegalDataException("Customization price has to be non-negative.", OffsetDateTime.now(clock));
                }
            }
        }
    }
}
