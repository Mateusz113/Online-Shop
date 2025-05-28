package com.mateusz113.order_service_core.facade;

import com.mateusz113.order_service_core.port.incoming.OrderServiceEventPorts;
import com.mateusz113.order_service_core.port.outgoing.CartServiceCommunicator;
import com.mateusz113.order_service_core.port.outgoing.EmailSender;
import com.mateusz113.order_service_core.port.outgoing.EventSender;
import com.mateusz113.order_service_core.port.outgoing.InvoiceGenerator;
import com.mateusz113.order_service_core.port.outgoing.OrderServiceDatabase;
import com.mateusz113.order_service_core.port.outgoing.OrderServiceLogger;
import com.mateusz113.order_service_core.port.outgoing.ProductServiceCommunicator;
import com.mateusz113.order_service_core.verifier.OrderServiceVerifier;
import com.mateusz113.order_service_model.client.Address;
import com.mateusz113.order_service_model.client.Client;
import com.mateusz113.order_service_model.email.EmailData;
import com.mateusz113.order_service_model.event.EventType;
import com.mateusz113.order_service_model.exception.EmailMessagingException;
import com.mateusz113.order_service_model.exception.OrderDoesNotExistException;
import com.mateusz113.order_service_model.exception.OrderProcessingErrorException;
import com.mateusz113.order_service_model.exception.OrderStatusUpdateErrorException;
import com.mateusz113.order_service_model.invoice.InvoiceData;
import com.mateusz113.order_service_model.invoice.InvoiceItem;
import com.mateusz113.order_service_model.order.Order;
import com.mateusz113.order_service_model.order.OrderProcessingData;
import com.mateusz113.order_service_model.order.OrderStatus;
import com.mateusz113.order_service_model.order.OrderStatusUpdateData;
import com.mateusz113.order_service_model.product.Customization;
import com.mateusz113.order_service_model.product.CustomizationOption;
import com.mateusz113.order_service_model.product.Product;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
public class OrderServiceEventFacade implements OrderServiceEventPorts {
    private final CartServiceCommunicator cartServiceCommunicator;
    private final ProductServiceCommunicator productServiceCommunicator;
    private final OrderServiceDatabase database;
    private final OrderServiceVerifier verifier;
    private final InvoiceGenerator invoiceGenerator;
    private final EmailSender emailSender;
    private final OrderServiceLogger logger;
    private final EventSender eventSender;
    private final Clock clock;

    @Override
    public void orderPaidEvent(OrderStatusUpdateData updateData) {
        logger.info("Received ORDER-PAID-EVENT with data: %s".formatted(updateData.toString()));
        verifier.verifyOrderStatusUpdateData(updateData);
        Order order = getOrderById(updateData.orderId());
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new OrderStatusUpdateErrorException("The order is paid or cancelled already.", OffsetDateTime.now(clock));
        }
        Map<Long, Integer> soldProducts = order.getProducts().stream()
                .collect(Collectors.toMap(Product::getSourceId, Product::getQuantity, Integer::sum));
        productServiceCommunicator.updateSoldProductsStock(soldProducts);
        cartServiceCommunicator.deleteCart(order.getCartId());
        order.setOrderStatus(OrderStatus.PAID);
        database.save(order);
        eventSender.sendEvent(getProcessingData(order.getId(), EventType.GENERATE_INVOICE, updateData.client()));
    }

    @Override
    public void orderPaidEventDlt(OrderStatusUpdateData updateData) {
        logger.info("Received ORDER-PAID-EVENT-DLT with data: %s".formatted(updateData.toString()));
        verifier.verifyOrderStatusUpdateData(updateData);
        Order order = getOrderById(updateData.orderId());
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new OrderStatusUpdateErrorException("Cannot automatically cancel orders that were already cancelled or have confirmed payment.", OffsetDateTime.now(clock));
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        database.save(order);
        eventSender.sendEvent(getProcessingData(order.getId(), EventType.SEND_EMAIL, updateData.client()));
    }

    @Override
    public void orderShipmentEvent(OrderStatusUpdateData updateData) {
        logger.info("Received ORDER-SHIPMENT-EVENT with data: %s".formatted(updateData.toString()));
        verifier.verifyOrderStatusUpdateData(updateData);
        Order order = getOrderById(updateData.orderId());
        if (order.getOrderStatus() != OrderStatus.PAID) {
            throw new OrderStatusUpdateErrorException("The order must have confirmed payment and must not yet be shipped or delivered.", OffsetDateTime.now(clock));
        }
        order.setOrderStatus(OrderStatus.IN_SHIPPING);
        database.save(order);
        eventSender.sendEvent(getProcessingData(order.getId(), EventType.SEND_EMAIL, updateData.client()));
    }

    @Override
    public void orderDeliveredEvent(OrderStatusUpdateData updateData) {
        logger.info("Received ORDER-DELIVERED-EVENT with data: %s".formatted(updateData.toString()));
        verifier.verifyOrderStatusUpdateData(updateData);
        Order order = getOrderById(updateData.orderId());
        if (order.getOrderStatus() != OrderStatus.IN_SHIPPING) {
            throw new OrderStatusUpdateErrorException("The order has to be in in-shipment for it to be delivered.", OffsetDateTime.now(clock));
        }
        order.setOrderStatus(OrderStatus.DELIVERED);
        database.save(order);
        eventSender.sendEvent(getProcessingData(order.getId(), EventType.SEND_EMAIL, updateData.client()));
    }

    @Override
    public void generateInvoiceEvent(OrderProcessingData processingData) {
        logger.info("Received GENERATE-INVOICE-EVENT with data: %s".formatted(processingData.toString()));
        Order order = getOrderById(processingData.orderId());
        if (order.getOrderStatus() == OrderStatus.PENDING) {
            throw new OrderProcessingErrorException("Cannot generate invoice for order without confirmed payment.", OffsetDateTime.now(clock));
        }
        InvoiceData invoiceData = getInvoiceData(processingData.client(), order, clock);
        byte[] invoicePdf = invoiceGenerator.generateInvoice(invoiceData);
        order.setInvoice(invoicePdf);
        database.save(order);
        eventSender.sendEvent(getProcessingData(order.getId(), EventType.SEND_EMAIL_WITH_INVOICE, processingData.client()));
    }

    @Override
    public void sendEmailEvent(OrderProcessingData processingData) {
        logger.info("Received SEND-EMAIL-EVENT with data: %s".formatted(processingData.toString()));
        Order order = getOrderById(processingData.orderId());
        EmailData emailData = getEmailData(order.getOrderStatus(), processingData.client());
        emailSender.sendEmail(emailData);
    }

    @Override
    public void sendEmailWithInvoiceEvent(OrderProcessingData processingData) {
        logger.info("Received SEND-EMAIL-WITH-INVOICE-EVENT with data: %s".formatted(processingData.toString()));
        Order order = getOrderById(processingData.orderId());
        EmailData emailData = getEmailData(order.getOrderStatus(), processingData.client());
        String attachmentName = "%s.pdf".formatted(getInvoiceNumber(order.getId(), clock));
        emailSender.sendEmailWithInvoice(emailData, order.getInvoice(), attachmentName);
    }

    private OrderProcessingData getProcessingData(Long orderId, EventType type, Client client) {
        return OrderProcessingData.builder()
                .orderId(orderId)
                .eventType(type)
                .client(client)
                .build();
    }

    private Order getOrderById(Long orderId) {
        return database.findById(orderId)
                .orElseThrow(() -> new OrderDoesNotExistException("Order with id: %d does not exist.".formatted(orderId), OffsetDateTime.now(clock)));
    }

    private EmailData getEmailData(OrderStatus status, Client client) {
        return EmailData.builder()
                .recipientEmail(client.email())
                .subject(getEmailSubject(status))
                .message(getEmailMessage(client.firstName(), status))
                .build();
    }

    private String getEmailSubject(OrderStatus status) {
        return switch (status) {
            case PAID -> "Order confirmation";
            case IN_SHIPPING -> "Shipment confirmation";
            case DELIVERED -> "Delivery confirmation";
            case CANCELLED -> "Order cancellation";
            case PENDING -> throw new OrderProcessingErrorException("Cannot send email for pending orders.", OffsetDateTime.now(clock));
        };
    }

    private String getEmailMessage(String clientFirstName, OrderStatus status) {
        return String.format(switch (status) {
            case PAID -> """
                    Dear %s,
                    
                    We’re pleased to confirm that your order has been placed successfully.
                    
                    Please find the invoice for your order attached to this message.
                    If you have any questions or need further assistance, feel free to reach out to us.
                    
                    Best regards,
                    Online Shop Team
                    """;
            case IN_SHIPPING -> """
                    Dear %s,
                    
                    We’re excited to let you know that your order has been shipped and is currently on its way.
                    
                    You can expect delivery soon. If your order includes tracking information, it will be available in your account or provided separately.
                    
                    If you have any questions or need support, feel free to contact us.
                    
                    Best regards,
                    Online Shop Team
                    """;
            case DELIVERED -> """
                    Dear %s,
                    
                    We’re pleased to inform you that your order has been successfully delivered.
                    
                    We hope you’re happy with your purchase. If you have any feedback or need assistance with your order, don’t hesitate to reach out.
                    
                    Thank you for shopping with us!
                    
                    Best regards,
                    Online Shop Team
                    """;
            case CANCELLED -> """
                    Dear %s,
                    
                    We regret to inform you that your order has been cancelled, as one or more of the products you selected are currently out of stock.
                    
                    Any payment made for this order will be refunded promptly to your original payment method. You will receive a confirmation once the refund has been processed.
                    
                    We sincerely apologize for the inconvenience. If you have any questions or would like assistance with placing a new order, please don’t hesitate to contact us.
                    
                    Thank you for your understanding.
                    
                    Best regards,
                    Online Shop Team
                    """;
            case PENDING -> "";
        }, clientFirstName);
    }

    private InvoiceData getInvoiceData(
            Client client,
            Order order,
            Clock clock
    ) {
        List<InvoiceItem> invoiceItems = getInvoiceItemsFromProducts(order.getProducts());
        return InvoiceData.builder()
                .from("Online Shop")
                .to("%s %s".formatted(client.firstName(), client.lastName()))
                .shipTo(getShipmentDataString(client.address()))
                .number(getInvoiceNumber(order.getId(), clock))
                .items(invoiceItems)
                .amountPaid(calculateTotalPaidAmount(invoiceItems))
                .build();
    }

    private String getShipmentDataString(Address address) {
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

    private String getInvoiceNumber(Long orderId, Clock clock) {
        OffsetDateTime currentTime = OffsetDateTime.now(clock);
        return "PL/%d/%d/%d/%d".formatted(currentTime.getDayOfMonth(), currentTime.getMonth().getValue(), currentTime.getYear(), orderId);
    }

    private List<InvoiceItem> getInvoiceItemsFromProducts(List<Product> products) {
        return products.stream()
                .map(this::getInvoiceItemFromProduct)
                .toList();
    }

    private InvoiceItem getInvoiceItemFromProduct(Product product) {
        return InvoiceItem.builder()
                .name("%s %s".formatted(product.getBrand(), product.getName()))
                .quantity(product.getQuantity())
                .price(getProductFinalPrice(product))
                .description(getProductDescription(product))
                .build();
    }

    private BigDecimal calculateTotalPaidAmount(List<InvoiceItem> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem item : items) {
            BigDecimal itemTotalPrice = item.price().multiply(BigDecimal.valueOf(item.quantity()));
            total = total.add(itemTotalPrice);
        }
        return total;
    }

    private BigDecimal getProductFinalPrice(Product product) {
        BigDecimal finalPrice = product.getPrice();
        for (Customization customization : product.getCustomizations()) {
            for (CustomizationOption option : customization.getOptions()) {
                finalPrice = finalPrice.add(option.getPriceDifference());
            }
        }
        return finalPrice.setScale(2, RoundingMode.HALF_UP);
    }

    private String getProductDescription(Product product) {
        List<String> descriptionPoints = new ArrayList<>();
        if (!product.getCustomizations().isEmpty()) {
            descriptionPoints.add("%s %s with:\n".formatted(product.getBrand(), product.getName()));
            product.getCustomizations().forEach(customization -> {
                customization.getOptions().forEach(option -> {
                    String point = "- %s: %s".formatted(customization.getName(), option.getName());
                    if (option.getPriceDifference().compareTo(BigDecimal.ZERO) > 0) {
                        point += " (+%.2f)\n".formatted(option.getPriceDifference());
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
