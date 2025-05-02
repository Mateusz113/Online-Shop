package com.mateusz113.product_service_adapters.util;

import com.mateusz113.product_service_adapters.entity.CustomizationElementEntity;
import com.mateusz113.product_service_adapters.entity.CustomizationOptionEntity;
import com.mateusz113.product_service_adapters.entity.ProductDetailEntity;
import com.mateusz113.product_service_adapters.entity.ProductEntity;
import com.mateusz113.product_service_model.content_management.PageableContent;
import com.mateusz113.product_service_model.customization.CustomizationElement;
import com.mateusz113.product_service_model.customization.CustomizationOption;
import com.mateusz113.product_service_model.product.Product;
import com.mateusz113.product_service_model.product.ProductDetail;
import com.mateusz113.product_service_model_public.commands.UpsertCustomizationElementCommand;
import com.mateusz113.product_service_model_public.commands.UpsertCustomizationOptionCommand;
import com.mateusz113.product_service_model_public.commands.UpsertProductCommand;
import com.mateusz113.product_service_model_public.commands.UpsertProductDetailCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public class ProductServiceAdaptersTestUtil {
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

    public static ProductEntity getProductEntity() {
        return ProductEntity.builder()
                .id(1L)
                .name("name")
                .brand("brand")
                .price(getDefaultPrice())
                .type("type")
                .availableAmount(getDefaultAvailableAmount())
                .details(List.of(getProductDetailEntity(), getProductDetailEntity()))
                .customizations(List.of(getCustomizationElementEntity(), getCustomizationElementEntity()))
                .build();
    }

    public static ProductDetail getProductDetail() {
        return ProductDetail.builder()
                .id(1L)
                .label("label")
                .value("value")
                .build();
    }

    public static ProductDetailEntity getProductDetailEntity() {
        return ProductDetailEntity.builder()
                .id(1L)
                .label("label")
                .value("value")
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

    public static CustomizationElementEntity getCustomizationElementEntity() {
        return CustomizationElementEntity.builder()
                .id(1L)
                .name("name")
                .multipleChoice(true)
                .options(List.of(getCustomizationOptionEntity(), getCustomizationOptionEntity()))
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

    public static CustomizationOptionEntity getCustomizationOptionEntity() {
        return CustomizationOptionEntity.builder()
                .id(1L)
                .name("name")
                .defaultOption(false)
                .priceDifference(getDefaultPriceDifference())
                .build();
    }

    public static Page<ProductEntity> getPageOfProductEntity(int pageNumber, int pageSize) {
        return new PageImpl<>(List.of(getProductEntity(), getProductEntity()), PageRequest.of(pageNumber, pageSize), 2);
    }

    public static UpsertProductCommand getUpsertProductCommand() {
        return UpsertProductCommand.builder()
                .name("name")
                .brand("brand")
                .price(getDefaultPrice())
                .type("type")
                .availableAmount(getDefaultAvailableAmount())
                .details(List.of(getUpsertProductDetailCommand(), getUpsertProductDetailCommand()))
                .customizations(List.of(getUpsertCustomizationElementCommand(), getUpsertCustomizationElementCommand()))
                .build();
    }

    public static UpsertProductDetailCommand getUpsertProductDetailCommand() {
        return UpsertProductDetailCommand.builder()
                .label("label")
                .value("value")
                .build();
    }

    public static UpsertCustomizationElementCommand getUpsertCustomizationElementCommand() {
        return UpsertCustomizationElementCommand.builder()
                .name("name")
                .multipleChoice(true)
                .options(List.of(getUpsertCustomizationOptionCommand(), getUpsertCustomizationOptionCommand()))
                .build();
    }

    public static UpsertCustomizationOptionCommand getUpsertCustomizationOptionCommand() {
        return UpsertCustomizationOptionCommand.builder()
                .name("name")
                .defaultOption(false)
                .priceDifference(getDefaultPriceDifference())
                .build();
    }

    public static PageableContent<Product> getProductPageableContent(Pageable pageable) {
        return PageableContent.<Product>builder()
                .totalElements(2)
                .totalPages((int) Math.ceil((double) 2 / pageable.getPageSize()))
                .pageSize(pageable.getPageSize())
                .pageNumber(pageable.getPageNumber())
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
}
