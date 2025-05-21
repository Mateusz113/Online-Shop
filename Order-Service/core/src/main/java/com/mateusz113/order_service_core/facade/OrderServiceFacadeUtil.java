package com.mateusz113.order_service_core.facade;

import com.mateusz113.order_service_model.client.Address;
import com.mateusz113.order_service_model.client.Client;
import com.mateusz113.order_service_model.invoice.InvoiceData;
import com.mateusz113.order_service_model.invoice.InvoiceItem;
import com.mateusz113.order_service_model.product.Customization;
import com.mateusz113.order_service_model.product.CustomizationOption;
import com.mateusz113.order_service_model.product.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

public class OrderServiceFacadeUtil {
    public static InvoiceData getInvoiceData(
            Client client,
            Long orderId,
            List<Product> products,
            Clock clock
    ) {
        return InvoiceData.builder()
                .from("Online Shop")
                .to("%s %s".formatted(client.firstName(), client.lastName()))
                .shipTo(getShipmentDataString(client.address()))
                .number(getInvoiceNumber(orderId, clock))
                .items(getInvoiceItemsFromProducts(products))
                .build();
    }

    public static String getInvoiceNumber(Long orderId, Clock clock) {
        OffsetDateTime currentTime = OffsetDateTime.now(clock);
        return "PL/%d/%d/%d/%d".formatted(currentTime.getDayOfMonth(), currentTime.getMonth().getValue(), currentTime.getYear(), orderId);
    }

    private static String getShipmentDataString(Address address) {
        List<String> addressElements = new ArrayList<>();
        addressElements.add("Country: %s\n".formatted(address.country()));
        addressElements.add("City: %s\n".formatted(address.city()));
        addressElements.add("Zip code: %s\n".formatted(address.zipCode()));
        addressElements.add("Street: %s\n".formatted(address.street()));
        addressElements.add("Building number: %s".formatted(address.buildingNumber()));
        if (nonNull(address.apartmentNumber())) {
            addressElements.add("/%s".formatted(address.apartmentNumber()));
        }
        return addressElements.stream()
                .reduce(String::concat)
                .toString();
    }

    private static List<InvoiceItem> getInvoiceItemsFromProducts(List<Product> products) {
        return products.stream()
                .map(OrderServiceFacadeUtil::getInvoiceItemFromProduct)
                .toList();
    }

    private static InvoiceItem getInvoiceItemFromProduct(Product product) {
        return InvoiceItem.builder()
                .name("%s %s".formatted(product.getBrand(), product.getName()))
                .quantity(product.getQuantity())
                .price(getProductFinalPrice(product))
                .description(getProductDescription(product))
                .build();
    }

    private static BigDecimal getProductFinalPrice(Product product) {
        BigDecimal finalPrice = product.getPrice();
        for (Customization customization : product.getCustomizations()) {
            for (CustomizationOption option : customization.getOptions()) {
                finalPrice = finalPrice.add(option.getPriceDifference());
            }
        }
        return finalPrice.setScale(2, RoundingMode.HALF_UP);
    }

    private static String getProductDescription(Product product) {
        List<String> descriptionPoints = new ArrayList<>();
        if (!product.getCustomizations().isEmpty()) {
            descriptionPoints.add("%s %s with:\n".formatted(product.getBrand(), product.getName()));
            product.getCustomizations().forEach(customization -> {
                customization.getOptions().forEach(option -> {
                    String point = "- %s: %s".formatted(customization.getName(), option.getName());
                    if (option.getPriceDifference().compareTo(BigDecimal.ZERO) > 0) {
                        point += " (+ %b)".formatted(option.getPriceDifference());
                    }
                    descriptionPoints.add(point);
                });
            });
        }
        return descriptionPoints.stream()
                .reduce(String::concat)
                .toString();
    }
}
