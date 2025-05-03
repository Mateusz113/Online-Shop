package com.mateusz113.product_service_core;

import com.mateusz113.product_service_model.customization.CustomizationElement;
import com.mateusz113.product_service_model.customization.CustomizationOption;
import com.mateusz113.product_service_model.exception.CustomizationElementIllegalDataException;
import com.mateusz113.product_service_model.exception.CustomizationOptionIllegalDataException;
import com.mateusz113.product_service_model.exception.ProductDetailIllegalDataException;
import com.mateusz113.product_service_model.exception.ProductFilterIllegalDataException;
import com.mateusz113.product_service_model.exception.ProductIllegalDataException;
import com.mateusz113.product_service_model.filter.ProductFilter;
import com.mateusz113.product_service_model.product.Product;
import com.mateusz113.product_service_model.product.ProductDetail;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class ProductServiceVerifier {
    private final Clock clock;

    public void verifyProductId(Long productId) {
        if (Objects.isNull(productId)) {
            throw new ProductIllegalDataException("Product ID cannot be null.", OffsetDateTime.now(clock));
        }
    }

    public void verifyProduct(Product product) {
        //null checks
        verifyNullableObjectAndItsFields(product);
        verifyNullableDetailsAndItsFields(product.getDetails());
        verifyNullableCustomizationsAndItsFields(product.getCustomizations());
        //logic checks
        verifyPrice(product.getPrice());
        verifyAvailableAmount(product.getAvailableAmount());
        verifyCustomizations(product.getCustomizations());
    }

    public void verifyProductList(List<Product> products) {
        if (Objects.isNull(products)) {
            throw new ProductIllegalDataException("Product and its fields cannot be null.", OffsetDateTime.now(clock));
        }
        for (Product product : products) {
            verifyProduct(product);
        }
    }

    public void verifyProductFilter(ProductFilter filter) {
        if (Objects.isNull(filter)) {
            throw new ProductFilterIllegalDataException("Product filter cannot be null.", OffsetDateTime.now(clock));
        }
        verifyProductFilterPrices(filter);
        verifyProductFilterAvailableAmount(filter);
    }

    private void verifyNullableObjectAndItsFields(Product product) {
        if (Objects.isNull(product)
                || Objects.isNull(product.getName())
                || Objects.isNull(product.getBrand())
                || Objects.isNull(product.getPrice())
                || Objects.isNull(product.getType())
                || Objects.isNull(product.getDetails())
                || Objects.isNull(product.getCustomizations())
        ) {
            throw new ProductIllegalDataException("Product and its fields cannot be null.", OffsetDateTime.now(clock));
        }
    }

    private void verifyNullableDetailsAndItsFields(List<ProductDetail> productDetails) {
        if (Objects.isNull(productDetails)) {
            throw new ProductDetailIllegalDataException("Product detail and its fields cannot be null.", OffsetDateTime.now(clock));
        }
        for (ProductDetail detail : productDetails) {
            if (Objects.isNull(detail)
                    || Objects.isNull(detail.getLabel())
                    || Objects.isNull(detail.getDescription())) {
                throw new ProductDetailIllegalDataException("Product detail and its fields cannot be null.", OffsetDateTime.now(clock));
            }
        }
    }

    private void verifyNullableCustomizationsAndItsFields(List<CustomizationElement> customizations) {
        for (CustomizationElement customization : customizations) {
            if (Objects.isNull(customization)
                    || Objects.isNull(customization.getName())
                    || Objects.isNull(customization.getMultipleChoice())
                    || Objects.isNull(customization.getOptions())) {
                throw new CustomizationElementIllegalDataException("Customization element and its fields cannot be null.", OffsetDateTime.now(clock));
            }
            verifyNonNullCustomizationOptionsFields(customization.getOptions());
        }
    }

    private void verifyNonNullCustomizationOptionsFields(List<CustomizationOption> options) {
        for (CustomizationOption option : options) {
            if (Objects.isNull(option)
                    || Objects.isNull(option.getName())
                    || Objects.isNull(option.getDefaultOption())
                    || Objects.isNull(option.getPriceDifference())) {
                throw new CustomizationOptionIllegalDataException("Customization option and its fields cannot be null.", OffsetDateTime.now(clock));
            }
        }
    }

    private void verifyPrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
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

    private void verifyProductFilterPrices(ProductFilter filter) {
        if (Objects.nonNull(filter.minPrice()) && filter.minPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ProductFilterIllegalDataException("Minimum price has to be greater or equal to 0.", OffsetDateTime.now(clock));
        }
        if (Objects.nonNull(filter.maxPrice()) && filter.maxPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ProductFilterIllegalDataException("Maximum price has to be greater or equal to 0.", OffsetDateTime.now(clock));
        }
        if (Objects.nonNull(filter.minPrice())
                && Objects.nonNull(filter.maxPrice())
                && filter.minPrice().compareTo(filter.maxPrice()) > 0
        ) {
            throw new ProductFilterIllegalDataException("Minimum price has to be less or equal to maximum price.", OffsetDateTime.now(clock));
        }
    }

    private void verifyProductFilterAvailableAmount(ProductFilter filter) {
        if (Objects.nonNull(filter.minAvailableAmount()) && filter.minAvailableAmount() < 0) {
            throw new ProductFilterIllegalDataException("Minimum available amount has to be greater than 0.", OffsetDateTime.now(clock));
        }
    }

    private void verifyCustomizations(List<CustomizationElement> customizations) {
        for (CustomizationElement customization : customizations) {
            long defaultOptionsCount = customization.getOptions().stream()
                    .filter(CustomizationOption::getDefaultOption)
                    .count();
            if (defaultOptionsCount > 1) {
                throw new CustomizationElementIllegalDataException("There cannot be more than one default option for each customization.", OffsetDateTime.now(clock));
            }
            if (!customization.getMultipleChoice() && defaultOptionsCount == 0) {
                throw new CustomizationElementIllegalDataException("There has to be a default option for single choice customizations.", OffsetDateTime.now(clock));
            }
            for (CustomizationOption option : customization.getOptions()) {
                if (option.getPriceDifference().compareTo(BigDecimal.ZERO) < 0) {
                    throw new CustomizationOptionIllegalDataException("Customization price has to be non-negative.", OffsetDateTime.now(clock));
                }
            }
        }
    }
}
