package com.mateusz113.product_service_core.util;

import com.mateusz113.product_service_model.content_management.PageableContent;
import com.mateusz113.product_service_model.customization.CustomizationElement;
import com.mateusz113.product_service_model.customization.CustomizationOption;
import com.mateusz113.product_service_model.product.Product;
import com.mateusz113.product_service_model.product.ProductDetail;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class ProductServiceCoreTestUtil {
    public static Product getProduct() {
        return Product.builder()
                .id(1L)
                .name("name")
                .brand("brand")
                .price(getDefaultPrice())
                .type("type")
                .availableAmount(getDefaultAvailableAmount())
                .details(List.of(getProductDetail(), getProductDetail()))
                .customizations(List.of(getCustomizationElement(), getCustomizationElement()))
                .build();
    }

    public static ProductDetail getProductDetail() {
        return ProductDetail.builder()
                .id(1L)
                .label("label")
                .description("description")
                .build();
    }

    public static CustomizationElement getCustomizationElement() {
        return CustomizationElement.builder()
                .id(1L)
                .name("name")
                .multipleChoice(true)
                .options(List.of(getCustomizationOption(), getCustomizationOption()))
                .build();
    }

    public static CustomizationOption getCustomizationOption() {
        return CustomizationOption.builder()
                .id(1L)
                .name("name")
                .defaultOption(false)
                .priceDifference(getDefaultPriceDifference())
                .build();
    }

    public static PageableContent<Product> getProductPageableContent(int pageNumber, int pageSize) {
        return PageableContent.<Product>builder()
                .totalElements(2L)
                .totalPages((int) Math.ceil((double) 2 / pageSize))
                .pageSize(pageSize)
                .pageNumber(pageNumber)
                .elements(List.of(getProduct(), getProduct()))
                .build();
    }

    public static BigDecimal getDefaultPrice() {
        return BigDecimal.TEN;
    }

    public static BigDecimal getDefaultPriceDifference() {
        return BigDecimal.ONE;
    }

    public static int getDefaultAvailableAmount() {
        return 10;
    }

    public static Clock getClock() {
        return Clock.fixed(Instant.parse("2012-12-12T12:00:00Z"), ZoneOffset.UTC);
    }

    public static OffsetDateTime getDefaultTime() {
        return OffsetDateTime.now(getClock());
    }
}
