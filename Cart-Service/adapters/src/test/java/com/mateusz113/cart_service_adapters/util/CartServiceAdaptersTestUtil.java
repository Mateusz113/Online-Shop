package com.mateusz113.cart_service_adapters.util;

import com.mateusz113.cart_service_adapters.entity.AppliedCustomizationEntity;
import com.mateusz113.cart_service_adapters.entity.AppliedCustomizationOptionEntity;
import com.mateusz113.cart_service_adapters.entity.CartEntity;
import com.mateusz113.cart_service_adapters.entity.CustomizedProductEntity;
import com.mateusz113.cart_service_model.cart.Cart;
import com.mateusz113.cart_service_model.customization.AppliedCustomization;
import com.mateusz113.cart_service_model.customization.AppliedCustomizationOption;
import com.mateusz113.cart_service_model.customization.SourceCustomizationElement;
import com.mateusz113.cart_service_model.customization.SourceCustomizationOption;
import com.mateusz113.cart_service_model.product.CustomizedProduct;
import com.mateusz113.cart_service_model.product.SourceProduct;
import com.mateusz113.cart_service_model_public.command.InsertAppliedCustomizationCommand;
import com.mateusz113.cart_service_model_public.command.InsertAppliedCustomizationOptionCommand;
import com.mateusz113.cart_service_model_public.command.InsertCartCommand;
import com.mateusz113.cart_service_model_public.command.InsertCustomizedProductCommand;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class CartServiceAdaptersTestUtil {
    public static Cart getCart() {
        return Cart.builder()
                .id(1L)
                .creationDate(getDefaultTime())
                .customizedProducts(new ArrayList<>(List.of(getCustomizedProduct(), getCustomizedProduct())))
                .build();
    }

    public static Cart getCartWithoutIds() {
        Cart cart = getCart();
        cart.setId(null);
        cart.getCustomizedProducts().forEach(product -> {
            product.setId(null);
            product.getAppliedCustomizations().forEach(customization -> {
                customization.setId(null);
                customization.getAppliedOptions().forEach(option -> option.setId(null));
            });
        });
        return cart;
    }

    public static CustomizedProduct getCustomizedProduct() {
        return CustomizedProduct.builder()
                .id(1L)
                .sourceId(1L)
                .name("name")
                .brand("brand")
                .price(getDefaultPrice())
                .quantity(getDefaultQuantity())
                .appliedCustomizations(new ArrayList<>(List.of(getAppliedCustomization(), getAppliedCustomization())))
                .build();
    }

    public static AppliedCustomization getAppliedCustomization() {
        return AppliedCustomization.builder()
                .id(1L)
                .sourceId(1L)
                .name("name")
                .multipleChoice(true)
                .appliedOptions(List.of(getAppliedCustomizationOption(), getAppliedCustomizationOption()))
                .build();
    }

    public static AppliedCustomizationOption getAppliedCustomizationOption() {
        return AppliedCustomizationOption.builder()
                .id(1L)
                .sourceId(1L)
                .name("name")
                .priceDifference(getDefaultPriceDifference())
                .build();
    }

    public static CartEntity getCartEntity() {
        return CartEntity.builder()
                .id(1L)
                .creationDate(getDefaultTime())
                .customizedProducts(new ArrayList<>(List.of(getCustomizedProductEntity(), getCustomizedProductEntity())))
                .build();
    }

    public static CustomizedProductEntity getCustomizedProductEntity() {
        return CustomizedProductEntity.builder()
                .id(1L)
                .sourceId(1L)
                .name("name")
                .brand("brand")
                .price(getDefaultPrice())
                .quantity(getDefaultQuantity())
                .appliedCustomizations(new ArrayList<>(List.of(getAppliedCustomizationEntity(), getAppliedCustomizationEntity())))
                .build();
    }

    public static AppliedCustomizationEntity getAppliedCustomizationEntity() {
        return AppliedCustomizationEntity.builder()
                .id(1L)
                .sourceId(1L)
                .name("name")
                .multipleChoice(true)
                .appliedOptions(List.of(getAppliedCustomizationOptionEntity(), getAppliedCustomizationOptionEntity()))
                .build();
    }

    public static AppliedCustomizationOptionEntity getAppliedCustomizationOptionEntity() {
        return AppliedCustomizationOptionEntity.builder()
                .id(1L)
                .sourceId(1L)
                .name("name")
                .priceDifference(getDefaultPriceDifference())
                .build();
    }

    public static SourceProduct getSourceProduct() {
        return SourceProduct.builder()
                .id(1L)
                .name("source_name")
                .brand("source_brand")
                .price(getDefaultPrice())
                .availableAmount(getDefaultAvailableAmount())
                .customizations(List.of(getSourceCustomizationElement(), getSourceCustomizationElement()))
                .build();
    }

    public static SourceCustomizationElement getSourceCustomizationElement() {
        return SourceCustomizationElement.builder()
                .id(1L)
                .name("source_name")
                .multipleChoice(true)
                .options(List.of(getSourceCustomizationOption(), getSourceCustomizationOption()))
                .build();
    }

    public static SourceCustomizationOption getSourceCustomizationOption() {
        return SourceCustomizationOption.builder()
                .id(1L)
                .name("source_name")
                .priceDifference(getDefaultPriceDifference())
                .build();
    }

    public static InsertCartCommand getInsertCartCommand() {
        return InsertCartCommand.builder()
                .customizedProducts(List.of(getInsertCustomizedProductCommand(), getInsertCustomizedProductCommand()))
                .build();
    }

    public static InsertCustomizedProductCommand getInsertCustomizedProductCommand() {
        return InsertCustomizedProductCommand.builder()
                .sourceId(1L)
                .quantity(getDefaultQuantity())
                .appliedCustomizations(List.of(getInsertAppliedCustomizationCommand(), getInsertAppliedCustomizationCommand()))
                .build();
    }

    public static InsertAppliedCustomizationCommand getInsertAppliedCustomizationCommand() {
        return InsertAppliedCustomizationCommand.builder()
                .sourceId(1L)
                .appliedOptions(List.of(getInsertAppliedCustomizationOptionCommand(), getInsertAppliedCustomizationOptionCommand()))
                .build();
    }

    public static InsertAppliedCustomizationOptionCommand getInsertAppliedCustomizationOptionCommand() {
        return InsertAppliedCustomizationOptionCommand.builder()
                .sourceId(1L)
                .build();
    }

    public static int getDefaultAvailableAmount() {
        return 10;
    }

    public static BigDecimal getDefaultPrice() {
        return BigDecimal.TEN;
    }

    public static BigDecimal getDefaultPriceDifference() {
        return BigDecimal.ONE;
    }

    public static int getDefaultQuantity() {
        return 9;
    }

    public static Clock getClock() {
        return Clock.fixed(Instant.parse(getDefaultTimeString()), ZoneOffset.UTC);
    }

    public static OffsetDateTime getDefaultTime() {
        return OffsetDateTime.now(getClock());
    }

    public static String getDefaultTimeString() {
        return "2012-12-12T12:00:00Z";
    }
}
