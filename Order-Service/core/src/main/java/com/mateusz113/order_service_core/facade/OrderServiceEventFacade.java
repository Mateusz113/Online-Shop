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
import com.mateusz113.order_service_model.client.Client;
import com.mateusz113.order_service_model.email.EmailData;
import com.mateusz113.order_service_model.event.EventType;
import com.mateusz113.order_service_model.exception.EmailMessagingException;
import com.mateusz113.order_service_model.exception.OrderDoesNotExistException;
import com.mateusz113.order_service_model.exception.OrderStatusAlreadyUpdatedException;
import com.mateusz113.order_service_model.invoice.InvoiceData;
import com.mateusz113.order_service_model.order.Order;
import com.mateusz113.order_service_model.order.OrderProcessingData;
import com.mateusz113.order_service_model.order.OrderStatus;
import com.mateusz113.order_service_model.order.OrderStatusUpdateData;
import com.mateusz113.order_service_model.product.Product;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mateusz113.order_service_core.facade.OrderServiceFacadeUtil.getInvoiceData;
import static com.mateusz113.order_service_core.facade.OrderServiceFacadeUtil.getInvoiceNumber;

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
        if (order.getOrderStatus() == OrderStatus.PAID) {
            throw new OrderStatusAlreadyUpdatedException("Order status is set to PAID already.", OffsetDateTime.now(clock));
        }
        Map<Long, Integer> soldProducts = order.getProducts().stream()
                .collect(Collectors.toMap(Product::getSourceId, Product::getQuantity));
        productServiceCommunicator.updateSoldProductsStock(soldProducts);
        cartServiceCommunicator.deleteCart(order.getCartId());
        order.setPlacementTime(OffsetDateTime.now(clock));
        order.setOrderStatus(OrderStatus.PAID);
        database.save(order);
        eventSender.sendEvent(getProcessingData(order, EventType.GENERATE_INVOICE, updateData.client()));
    }

    @Override
    public void orderPaidEventDlt(OrderStatusUpdateData updateData) {
        logger.info("Received ORDER-PAID-EVENT-DLT with data: %s".formatted(updateData.toString()));
        verifier.verifyOrderStatusUpdateData(updateData);
        Order order = getOrderById(updateData.orderId());
        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new OrderStatusAlreadyUpdatedException("Order status is set to CANCELLED already.", OffsetDateTime.now(clock));
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        database.save(order);
        eventSender.sendEvent(getProcessingData(order, EventType.SEND_EMAIL, updateData.client()));
    }

    @Override
    public void orderShipmentEvent(OrderStatusUpdateData updateData) {
        logger.info("Received ORDER-SHIPMENT-EVENT with data: %s".formatted(updateData.toString()));
        verifier.verifyOrderStatusUpdateData(updateData);
        Order order = getOrderById(updateData.orderId());
        if (order.getOrderStatus() == OrderStatus.IN_SHIPPING) {
            throw new OrderStatusAlreadyUpdatedException("Order status is set to IN-SHIPPING already.", OffsetDateTime.now(clock));
        }
        order.setOrderStatus(OrderStatus.IN_SHIPPING);
        database.save(order);
        eventSender.sendEvent(getProcessingData(order, EventType.SEND_EMAIL, updateData.client()));
    }

    @Override
    public void orderDeliveredEvent(OrderStatusUpdateData updateData) {
        logger.info("Received ORDER-DELIVERED-EVENT with data: %s".formatted(updateData.toString()));
        verifier.verifyOrderStatusUpdateData(updateData);
        Order order = getOrderById(updateData.orderId());
        if (order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new OrderStatusAlreadyUpdatedException("Order status is set to DELIVERED already.", OffsetDateTime.now(clock));
        }
        order.setOrderStatus(OrderStatus.DELIVERED);
        database.save(order);
        eventSender.sendEvent(getProcessingData(order, EventType.SEND_EMAIL, updateData.client()));
    }

    @Override
    public void generateInvoiceEvent(OrderProcessingData processingData) {
        logger.info("Received GENERATE-INVOICE-EVENT with data: %s".formatted(processingData.toString()));
        Order order = processingData.order();
        InvoiceData invoiceData = getInvoiceData(processingData.client(), order.getId(), order.getProducts(), clock);
        byte[] invoicePdf = invoiceGenerator.generateInvoice(invoiceData);
        order.setInvoice(invoicePdf);
        database.save(order);
        eventSender.sendEvent(getProcessingData(order, EventType.SEND_EMAIL_WITH_INVOICE, processingData.client()));
    }

    @Override
    public void sendEmailEvent(OrderProcessingData processingData) {
        logger.info("Received SEND-EMAIL-EVENT with data: %s".formatted(processingData.toString()));
        Order order = processingData.order();
        EmailData emailData = getEmailData(order.getOrderStatus(), processingData.client());
        emailSender.sendEmail(emailData);
    }

    @Override
    public void sendEmailWithInvoiceEvent(OrderProcessingData processingData) {
        logger.info("Received SEND-EMAIL-WITH-INVOICE-EVENT with data: %s".formatted(processingData.toString()));
        Order order = processingData.order();
        EmailData emailData = getEmailData(order.getOrderStatus(), processingData.client());
        String attachmentName = "%s.pdf".formatted(getInvoiceNumber(order.getId(), clock));
        emailSender.sendEmailWithInvoice(emailData, order.getInvoice(), attachmentName);
    }

    private OrderProcessingData getProcessingData(Order order, EventType type, Client client) {
        return OrderProcessingData.builder()
                .order(order)
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
            case PENDING ->
                    throw new EmailMessagingException("Should not be able to send email for PENDING order.", OffsetDateTime.now(clock));
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
}
