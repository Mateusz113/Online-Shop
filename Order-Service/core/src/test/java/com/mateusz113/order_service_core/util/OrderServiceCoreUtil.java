package com.mateusz113.order_service_core.util;

import com.mateusz113.order_service_model.client.Address;
import com.mateusz113.order_service_model.client.Client;
import com.mateusz113.order_service_model.content.PageableContent;
import com.mateusz113.order_service_model.event.EventType;
import com.mateusz113.order_service_model.order.Order;
import com.mateusz113.order_service_model.order.OrderProcessingData;
import com.mateusz113.order_service_model.order.OrderStatus;
import com.mateusz113.order_service_model.product.Customization;
import com.mateusz113.order_service_model.product.CustomizationOption;
import com.mateusz113.order_service_model.product.Product;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class OrderServiceCoreUtil {
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

    public static OrderProcessingData getOrderProcessingData(EventType eventType) {
        return OrderProcessingData.builder()
                .orderId(1L)
                .eventType(eventType)
                .client(getClient())
                .build();
    }

    public static Client getClient() {
        return Client.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .phoneNumber("phoneNumber")
                .address(getAddress())
                .build();
    }

    public static Address getAddress() {
        return Address.builder()
                .country("country")
                .city("city")
                .zipCode("zipCode")
                .street("street")
                .buildingNumber("buildingNumber")
                .apartmentNumber("apartmentNumber")
                .build();
    }

    public static Client getClientCustomized(
            String firstName,
            String lastName,
            String email,
            String phoneNumber
    ) {
        return Client.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phoneNumber(phoneNumber)
                .address(getAddress())
                .build();
    }

    public static Address getAddressCustomized(
            String country,
            String city,
            String zipCode,
            String street,
            String buildingNumber,
            String apartmentNumber
    ) {
        return Address.builder()
                .country(country)
                .city(city)
                .zipCode(zipCode)
                .street(street)
                .buildingNumber(buildingNumber)
                .apartmentNumber(apartmentNumber)
                .build();
    }

    public static PageableContent<Order> getPageableContent(int pageNumber, int pageSize) {
        return PageableContent.<Order>builder()
                .totalPages((int) Math.ceil((double) 2 / pageSize))
                .totalElements(2L)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .elements(List.of(getOrder(), getOrder()))
                .build();
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
