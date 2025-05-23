package com.mateusz113.order_service_adapters.util;

import com.mateusz113.order_service_model.invoice.InvoiceData;
import com.mateusz113.order_service_model.invoice.InvoiceItem;
import com.mateusz113.order_service_model.order.Order;
import com.mateusz113.order_service_model.order.OrderStatus;
import com.mateusz113.order_service_model.product.Customization;
import com.mateusz113.order_service_model.product.CustomizationOption;
import com.mateusz113.order_service_model.product.Product;
import com.mateusz113.order_service_model_public.dto.CartCustomizationDto;
import com.mateusz113.order_service_model_public.dto.CartCustomizationOptionDto;
import com.mateusz113.order_service_model_public.dto.CartDto;
import com.mateusz113.order_service_model_public.dto.CartProductDto;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

public class CartServiceAdaptersUtil {
    public static CartDto getCartDto() {
        return CartDto.builder()
                .customizedProducts(List.of(getCartProductDto(), getCartProductDto()))
                .build();
    }

    public static CartProductDto getCartProductDto() {
        return CartProductDto.builder()
                .id(1L)
                .sourceId(1L)
                .name("name")
                .brand("brand")
                .price(getDefaultPrice())
                .quantity(getDefaultQuantity())
                .appliedCustomizations(List.of(getCartCustomizationDto(), getCartCustomizationDto()))
                .build();
    }

    public static CartCustomizationDto getCartCustomizationDto() {
        return CartCustomizationDto.builder()
                .name("name")
                .multipleChoice(true)
                .appliedOptions(List.of(getCartCustomizationOptionDto(), getCartCustomizationOptionDto()))
                .build();
    }

    public static CartCustomizationOptionDto getCartCustomizationOptionDto() {
        return CartCustomizationOptionDto.builder()
                .name("name")
                .priceDifference(getDefaultPriceDifference())
                .build();
    }

    public static InvoiceData getInvoiceData() {
        return InvoiceData.builder()
                .from("from")
                .to("to")
                .shipTo("shipTo")
                .number("number")
                .items(List.of(getInvoiceItem(), getInvoiceItem()))
                .build();
    }

    public static InvoiceItem getInvoiceItem() {
        return InvoiceItem.builder()
                .name("name")
                .quantity(getDefaultQuantity())
                .price(getDefaultPrice())
                .description("description")
                .build();
    }

    public static Order getOrder() {
        return Order.builder()
                .id(1L)
                .cartId(1L)
                .clientId(1L)
                .clientComment("clientComment")
                .placementTime(getDefaultTime())
                .orderStatus(OrderStatus.PENDING)
                .invoice(null)
                .products(List.of(getProduct(), getProduct()))
                .build();
    }

    public static Product getProduct() {
        return Product.builder()
                .id(1L)
                .sourceId(1L)
                .name("name")
                .brand("brand")
                .price(getDefaultPrice())
                .quantity(getDefaultQuantity())
                .customizations(List.of(getCustomization(), getCustomization()))
                .build();
    }

    public static Customization getCustomization() {
        return Customization.builder()
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
                .priceDifference(getDefaultPriceDifference())
                .build();
    }

    public static Map<Long, Integer> getProductsStocksMap() {
        return Map.of(1L, 5, 2L, 5);
    }

    public static BigDecimal getDefaultPrice() {
        return BigDecimal.TEN;
    }

    public static BigDecimal getDefaultPriceDifference() {
        return BigDecimal.ONE;
    }

    public static int getDefaultQuantity() {
        return 10;
    }

    public static Clock getClock() {
        return Clock.fixed(Instant.parse("2012-12-12T12:00:00Z"), ZoneOffset.UTC);
    }

    public static OffsetDateTime getDefaultTime() {
        return OffsetDateTime.now(getClock());
    }
}
