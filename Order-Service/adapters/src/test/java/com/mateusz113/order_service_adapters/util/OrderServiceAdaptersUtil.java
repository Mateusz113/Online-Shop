package com.mateusz113.order_service_adapters.util;

import com.mateusz113.order_service_adapters.entity.CustomizationEntity;
import com.mateusz113.order_service_adapters.entity.CustomizationOptionEntity;
import com.mateusz113.order_service_adapters.entity.OrderEntity;
import com.mateusz113.order_service_adapters.entity.ProductEntity;
import com.mateusz113.order_service_model.client.Address;
import com.mateusz113.order_service_model.client.Client;
import com.mateusz113.order_service_model.content.PageableContent;
import com.mateusz113.order_service_model.email.EmailData;
import com.mateusz113.order_service_model.event.EventType;
import com.mateusz113.order_service_model.invoice.InvoiceData;
import com.mateusz113.order_service_model.invoice.InvoiceItem;
import com.mateusz113.order_service_model.order.Order;
import com.mateusz113.order_service_model.order.OrderProcessingData;
import com.mateusz113.order_service_model.order.OrderStatus;
import com.mateusz113.order_service_model.product.Customization;
import com.mateusz113.order_service_model.product.CustomizationOption;
import com.mateusz113.order_service_model.product.Product;
import com.mateusz113.order_service_model_public.command.InsertOrderCommand;
import com.mateusz113.order_service_model_public.command.ProcessOrderCommand;
import com.mateusz113.order_service_model_public.command.UpdateOrderStatusCommand;
import com.mateusz113.order_service_model_public.dto.AddressDto;
import com.mateusz113.order_service_model_public.dto.CartCustomizationDto;
import com.mateusz113.order_service_model_public.dto.CartCustomizationOptionDto;
import com.mateusz113.order_service_model_public.dto.CartDto;
import com.mateusz113.order_service_model_public.dto.CartProductDto;
import com.mateusz113.order_service_model_public.dto.ClientDto;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

public class OrderServiceAdaptersUtil {
    private OrderServiceAdaptersUtil() {
    }

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
        List<InvoiceItem> items = List.of(getInvoiceItem(), getInvoiceItem());
        BigDecimal amountPaid = getInvoiceItem().price().multiply(BigDecimal.TWO);

        return InvoiceData.builder()
                .from("from")
                .to("to")
                .shipTo("shipTo")
                .number("number")
                .currency("currency")
                .items(items)
                .amountPaid(amountPaid)
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

    public static Order getOrderWithoutIds() {
        Order order = getOrder();
        order.setId(null);
        order.getProducts().forEach(product -> {
            product.setId(null);
            product.getCustomizations().forEach(customization -> {
                customization.setId(null);
                customization.getOptions().forEach(option -> option.setId(null));
            });
        });
        return order;
    }

    public static Order getOrderWithoutIdsWithInvoice(byte[] invoice) {
        Order order = getOrder();
        order.setId(null);
        order.getProducts().forEach(product -> {
            product.setId(null);
            product.getCustomizations().forEach(customization -> {
                customization.setId(null);
                customization.getOptions().forEach(option -> option.setId(null));
            });
        });
        order.setInvoice(invoice);
        return order;
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

    public static OrderEntity getOrderEntity() {
        return OrderEntity.builder()
                .id(1L)
                .cartId(1L)
                .clientId(1L)
                .clientComment("clientComment")
                .placementTime(getDefaultTime())
                .orderStatus(OrderStatus.PENDING)
                .invoice(null)
                .products(List.of(getProductEntity(), getProductEntity()))
                .build();
    }

    public static ProductEntity getProductEntity() {
        return ProductEntity.builder()
                .id(1L)
                .sourceId(1L)
                .name("name")
                .brand("brand")
                .price(getDefaultPrice())
                .quantity(getDefaultQuantity())
                .customizations(List.of(getCustomizationEntity(), getCustomizationEntity()))
                .build();
    }

    public static CustomizationEntity getCustomizationEntity() {
        return CustomizationEntity.builder()
                .id(1L)
                .name("name")
                .multipleChoice(true)
                .options(List.of(getCustomizationOptionEntity(), getCustomizationOptionEntity()))
                .build();
    }

    public static CustomizationOptionEntity getCustomizationOptionEntity() {
        return CustomizationOptionEntity.builder()
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

    public static ClientDto getClientDto() {
        return ClientDto.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .phoneNumber("phoneNumber")
                .address(getAddressDto())
                .build();
    }

    public static AddressDto getAddressDto() {
        return AddressDto.builder()
                .country("country")
                .city("city")
                .zipCode("zipCode")
                .street("street")
                .buildingNumber("buildingNumber")
                .apartmentNumber("apartmentNumber")
                .build();
    }

    public static InsertOrderCommand getInsertOrderCommand() {
        return InsertOrderCommand.builder()
                .cartId(1L)
                .clientId(1L)
                .clientComment("clientComment")
                .build();
    }

    public static PageableContent<Order> getPageableContent() {
        return PageableContent.<Order>builder()
                .totalPages(1)
                .totalElements(2L)
                .pageNumber(0)
                .pageSize(2)
                .elements(List.of(getOrder(), getOrder()))
                .build();
    }

    public static UpdateOrderStatusCommand getUpdateOrderStatusCommand() {
        return UpdateOrderStatusCommand.builder()
                .orderId(1L)
                .clientDto(getClientDto())
                .build();
    }

    public static ProcessOrderCommand getProcessOrderCommand() {
        return ProcessOrderCommand.builder()
                .orderId(1L)
                .eventType(null)
                .clientDto(getClientDto())
                .build();
    }

    public static EmailData getEmailData() {
        return EmailData.builder()
                .recipientEmail("recipientEmail")
                .subject("subject")
                .message("message")
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

    public static String getDefaultTimeString() {
        return "2012-12-12T12:00:00Z";
    }
}
