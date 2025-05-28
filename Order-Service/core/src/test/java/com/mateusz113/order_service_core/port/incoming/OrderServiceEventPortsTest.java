package com.mateusz113.order_service_core.port.incoming;

import com.mateusz113.order_service_core.facade.OrderServiceEventFacade;
import com.mateusz113.order_service_core.port.outgoing.CartServiceCommunicator;
import com.mateusz113.order_service_core.port.outgoing.EmailSender;
import com.mateusz113.order_service_core.port.outgoing.EventSender;
import com.mateusz113.order_service_core.port.outgoing.InvoiceGenerator;
import com.mateusz113.order_service_core.port.outgoing.OrderServiceDatabase;
import com.mateusz113.order_service_core.port.outgoing.OrderServiceLogger;
import com.mateusz113.order_service_core.port.outgoing.ProductServiceCommunicator;
import com.mateusz113.order_service_core.verifier.OrderServiceVerifier;
import com.mateusz113.order_service_model.client.Address;
import com.mateusz113.order_service_model.email.EmailData;
import com.mateusz113.order_service_model.event.EventType;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.mateusz113.order_service_core.util.OrderServiceCoreUtil.getClient;
import static com.mateusz113.order_service_core.util.OrderServiceCoreUtil.getClock;
import static com.mateusz113.order_service_core.util.OrderServiceCoreUtil.getDefaultTime;
import static com.mateusz113.order_service_core.util.OrderServiceCoreUtil.getOrder;
import static com.mateusz113.order_service_core.util.OrderServiceCoreUtil.getOrderProcessingData;
import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OrderServiceEventPortsTest {
    private OrderServiceEventPorts eventPorts;
    private CartServiceCommunicator cartServiceCommunicator;
    private ProductServiceCommunicator productServiceCommunicator;
    private OrderServiceDatabase database;
    private InvoiceGenerator invoiceGenerator;
    private EmailSender emailSender;
    private EventSender eventSender;

    @BeforeEach
    void setUp() {
        Clock clock = getClock();
        OrderServiceVerifier verifier = new OrderServiceVerifier(clock);
        OrderServiceLogger logger = mock(OrderServiceLogger.class);
        cartServiceCommunicator = mock(CartServiceCommunicator.class);
        productServiceCommunicator = mock(ProductServiceCommunicator.class);
        database = mock(OrderServiceDatabase.class);
        invoiceGenerator = mock(InvoiceGenerator.class);
        emailSender = mock(EmailSender.class);
        eventSender = mock(EventSender.class);
        eventPorts = new OrderServiceEventFacade(cartServiceCommunicator, productServiceCommunicator, database, verifier, invoiceGenerator, emailSender, logger, eventSender, clock);
    }

    @Test
    void orderPaidEvent_OrderWithIdNotFound_ThrowsOrderDoesNotExistException() {
        Long orderId = 1L;
        OrderStatusUpdateData data = new OrderStatusUpdateData(orderId, getClient());
        when(database.findById(orderId)).thenReturn(Optional.empty());

        OrderDoesNotExistException exception = assertThrows(OrderDoesNotExistException.class, () -> eventPorts.orderPaidEvent(data));

        assertEquals("Order with id: 1 does not exist.", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void orderPaidEvent_OrderStatusIsNotPending_ThrowsOrderStatusUpdateErrorException() {
        Long orderId = 1L;
        Order order = getOrder();
        order.setOrderStatus(OrderStatus.PAID);
        OrderStatusUpdateData data = new OrderStatusUpdateData(orderId, getClient());
        when(database.findById(orderId)).thenReturn(Optional.of(order));

        OrderStatusUpdateErrorException exception = assertThrows(OrderStatusUpdateErrorException.class, () -> eventPorts.orderPaidEvent(data));

        assertEquals("The order is paid or cancelled already.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void orderPaidEvent_DataIsCorrect_UpdatesOrderDataAndCallsOtherServices() {
        Long orderId = 1L;
        Order order = getOrder();
        Order updatedOrder = getOrder();
        updatedOrder.setOrderStatus(OrderStatus.PAID);
        OrderStatusUpdateData data = new OrderStatusUpdateData(orderId, getClient());
        when(database.findById(orderId)).thenReturn(Optional.of(order));

        eventPorts.orderPaidEvent(data);

        verify(productServiceCommunicator).updateSoldProductsStock(Map.of(1L, 20));
        verify(cartServiceCommunicator).deleteCart(1L);
        verify(database).save(updatedOrder);
        verify(eventSender).sendEvent(new OrderProcessingData(updatedOrder.getId(), EventType.GENERATE_INVOICE, data.client()));
    }

    @Test
    void orderPaidEventDlt_OrderWithIdNotFound_ThrowsOrderDoesNotExistException() {
        Long orderId = 1L;
        OrderStatusUpdateData data = new OrderStatusUpdateData(orderId, getClient());
        when(database.findById(orderId)).thenReturn(Optional.empty());

        OrderDoesNotExistException exception = assertThrows(OrderDoesNotExistException.class, () -> eventPorts.orderPaidEventDlt(data));

        assertEquals("Order with id: 1 does not exist.", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void orderPaidEventDlt_OrderStatusIsNotPending_ThrowsOrderStatusUpdateErrorException() {
        Long orderId = 1L;
        Order order = getOrder();
        order.setOrderStatus(OrderStatus.PAID);
        OrderStatusUpdateData data = new OrderStatusUpdateData(orderId, getClient());
        when(database.findById(orderId)).thenReturn(Optional.of(order));

        OrderStatusUpdateErrorException exception = assertThrows(OrderStatusUpdateErrorException.class, () -> eventPorts.orderPaidEventDlt(data));

        assertEquals("Cannot automatically cancel orders that were already cancelled or have confirmed payment.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void orderPaidEventDlt_DataIsCorrect_UpdatesOrderDataAndCallsEmailSender() {
        Long orderId = 1L;
        Order order = getOrder();
        Order updatedOrder = getOrder();
        updatedOrder.setOrderStatus(OrderStatus.CANCELLED);
        OrderStatusUpdateData data = new OrderStatusUpdateData(orderId, getClient());
        when(database.findById(orderId)).thenReturn(Optional.of(order));

        eventPorts.orderPaidEventDlt(data);

        verify(database).save(updatedOrder);
        verify(eventSender).sendEvent(new OrderProcessingData(updatedOrder.getId(), EventType.SEND_EMAIL, data.client()));
    }

    @Test
    void orderShipmentEvent_OrderWithIdNotFound_ThrowsOrderDoesNotExistException() {
        Long orderId = 1L;
        OrderStatusUpdateData data = new OrderStatusUpdateData(orderId, getClient());
        when(database.findById(orderId)).thenReturn(Optional.empty());

        OrderDoesNotExistException exception = assertThrows(OrderDoesNotExistException.class, () -> eventPorts.orderShipmentEvent(data));

        assertEquals("Order with id: 1 does not exist.", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void orderShipmentEvent_OrderStatusIsNotPaid_ThrowsOrderStatusUpdateErrorException() {
        Long orderId = 1L;
        Order order = getOrder();
        order.setOrderStatus(OrderStatus.IN_SHIPPING);
        OrderStatusUpdateData data = new OrderStatusUpdateData(orderId, getClient());
        when(database.findById(orderId)).thenReturn(Optional.of(order));

        OrderStatusUpdateErrorException exception = assertThrows(OrderStatusUpdateErrorException.class, () -> eventPorts.orderShipmentEvent(data));

        assertEquals("The order must have confirmed payment and must not yet be shipped or delivered.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void orderShipmentEvent_DataIsCorrect_UpdatesOrderDataAndCallsEmailSender() {
        Long orderId = 1L;
        Order order = getOrder();
        order.setOrderStatus(OrderStatus.PAID);
        Order updatedOrder = getOrder();
        updatedOrder.setOrderStatus(OrderStatus.IN_SHIPPING);
        OrderStatusUpdateData data = new OrderStatusUpdateData(orderId, getClient());
        when(database.findById(orderId)).thenReturn(Optional.of(order));

        eventPorts.orderShipmentEvent(data);

        verify(database).save(updatedOrder);
        verify(eventSender).sendEvent(new OrderProcessingData(updatedOrder.getId(), EventType.SEND_EMAIL, data.client()));
    }

    @Test
    void orderDeliveredEvent_OrderWithIdNotFound_ThrowsOrderDoesNotExistException() {
        Long orderId = 1L;
        OrderStatusUpdateData data = new OrderStatusUpdateData(orderId, getClient());
        when(database.findById(orderId)).thenReturn(Optional.empty());

        OrderDoesNotExistException exception = assertThrows(OrderDoesNotExistException.class, () -> eventPorts.orderDeliveredEvent(data));

        assertEquals("Order with id: 1 does not exist.", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void orderDeliveredEvent_OrderStatusIsNotInShipping_ThrowsOrderStatusUpdateErrorException() {
        Long orderId = 1L;
        Order order = getOrder();
        order.setOrderStatus(OrderStatus.DELIVERED);
        OrderStatusUpdateData data = new OrderStatusUpdateData(orderId, getClient());
        when(database.findById(orderId)).thenReturn(Optional.of(order));

        OrderStatusUpdateErrorException exception = assertThrows(OrderStatusUpdateErrorException.class, () -> eventPorts.orderDeliveredEvent(data));

        assertEquals("The order has to be in in-shipment for it to be delivered.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void orderDeliveredEvent_DataIsCorrect_UpdatesOrderDataAndCallsEmailSender() {
        Long orderId = 1L;
        Order order = getOrder();
        order.setOrderStatus(OrderStatus.IN_SHIPPING);
        Order updatedOrder = getOrder();
        updatedOrder.setOrderStatus(OrderStatus.DELIVERED);
        OrderStatusUpdateData data = new OrderStatusUpdateData(orderId, getClient());
        when(database.findById(orderId)).thenReturn(Optional.of(order));

        eventPorts.orderDeliveredEvent(data);

        verify(database).save(updatedOrder);
        verify(eventSender).sendEvent(new OrderProcessingData(updatedOrder.getId(), EventType.SEND_EMAIL, data.client()));
    }

    @Test
    void generateInvoiceEvent_OrderWithIdNotFound_ThrowsOrderProcessingErrorException() {
        OrderProcessingData data = getOrderProcessingData(EventType.GENERATE_INVOICE);
        when(database.findById(data.orderId())).thenReturn(Optional.empty());

        OrderDoesNotExistException exception = assertThrows(OrderDoesNotExistException.class, () -> eventPorts.generateInvoiceEvent(data));

        assertEquals("Order with id: 1 does not exist.", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void generateInvoiceEvent_OrderIsNotPaid_ThrowsOrderProcessingErrorException() {
        OrderProcessingData data = getOrderProcessingData(EventType.GENERATE_INVOICE);
        Order order = getOrder();
        when(database.findById(data.orderId())).thenReturn(Optional.of(order));

        OrderProcessingErrorException exception = assertThrows(OrderProcessingErrorException.class, () -> eventPorts.generateInvoiceEvent(data));

        assertEquals("Cannot generate invoice for order without confirmed payment.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void generateInvoiceEvent_DataIsCorrect_GeneratesOrderInvoiceAndCallsEmailSender() {
        OrderProcessingData data = getOrderProcessingData(EventType.GENERATE_INVOICE);
        byte[] invoice = new byte[0];
        Order order = getOrder();
        order.setOrderStatus(OrderStatus.PAID);
        Order updatedOrder = getOrder();
        updatedOrder.setOrderStatus(OrderStatus.PAID);
        updatedOrder.setInvoice(invoice);
        InvoiceData generatedInvoiceData = InvoiceData.builder()
                .from("Online Shop")
                .to("firstName lastName")
                .shipTo(getAddressString(data.client().address()))
                .number("PL/12/12/2012/1")
                .items(getInvoiceItemsFromProducts(order.getProducts()))
                .amountPaid(BigDecimal.valueOf(280).setScale(2, RoundingMode.HALF_UP))
                .build();
        when(database.findById(data.orderId())).thenReturn(Optional.of(order));
        when(invoiceGenerator.generateInvoice(generatedInvoiceData)).thenReturn(invoice);

        eventPorts.generateInvoiceEvent(data);

        verify(invoiceGenerator).generateInvoice(generatedInvoiceData);
        verify(database).save(updatedOrder);
        verify(eventSender).sendEvent(new OrderProcessingData(updatedOrder.getId(), EventType.SEND_EMAIL_WITH_INVOICE, data.client()));
    }

    @Test
    void sendEmailEvent_OrderWithIdNotFound_ThrowsOrderProcessingErrorException() {
        OrderProcessingData data = getOrderProcessingData(EventType.SEND_EMAIL);
        when(database.findById(data.orderId())).thenReturn(Optional.empty());

        OrderDoesNotExistException exception = assertThrows(OrderDoesNotExistException.class, () -> eventPorts.sendEmailEvent(data));

        assertEquals("Order with id: 1 does not exist.", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void sendEmailEvent_OrderHasPendingStatus_ThrowsOrderProcessingErrorException() {
        OrderProcessingData data = getOrderProcessingData(EventType.SEND_EMAIL);
        Order order = getOrder();
        when(database.findById(data.orderId())).thenReturn(Optional.of(order));

        OrderProcessingErrorException exception = assertThrows(OrderProcessingErrorException.class, () -> eventPorts.sendEmailEvent(data));

        assertEquals("Cannot send email for pending orders.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void sendEmailEvent_DataIsCorrect_SendsEmail() {
        OrderProcessingData data = getOrderProcessingData(EventType.SEND_EMAIL);
        Order order = getOrder();
        order.setOrderStatus(OrderStatus.PAID);
        when(database.findById(data.orderId())).thenReturn(Optional.of(order));
        EmailData emailData = EmailData.builder()
                .recipientEmail(data.client().email())
                .subject("Order confirmation")
                .message(getEmailMessageForPaidStatus())
                .build();

        eventPorts.sendEmailEvent(data);

        verify(emailSender).sendEmail(emailData);
    }

    @Test
    void sendEmailEventWithInvoice_OrderWithIdNotFound_ThrowsOrderProcessingErrorException() {
        OrderProcessingData data = getOrderProcessingData(EventType.SEND_EMAIL_WITH_INVOICE);
        when(database.findById(data.orderId())).thenReturn(Optional.empty());

        OrderDoesNotExistException exception = assertThrows(OrderDoesNotExistException.class, () -> eventPorts.sendEmailWithInvoiceEvent(data));

        assertEquals("Order with id: 1 does not exist.", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void sendEmailWithInvoiceEvent_OrderHasPendingStatus_ThrowsOrderProcessingErrorException() {
        OrderProcessingData data = getOrderProcessingData(EventType.SEND_EMAIL_WITH_INVOICE);
        Order order = getOrder();
        when(database.findById(data.orderId())).thenReturn(Optional.of(order));

        OrderProcessingErrorException exception = assertThrows(OrderProcessingErrorException.class, () -> eventPorts.sendEmailWithInvoiceEvent(data));

        assertEquals("Cannot send email for pending orders.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void sendEmailWithInvoiceEvent_DataIsCorrect_SendsEmailWithInvoice() {
        OrderProcessingData data = getOrderProcessingData(EventType.SEND_EMAIL_WITH_INVOICE);
        Order order = getOrder();
        order.setOrderStatus(OrderStatus.PAID);
        when(database.findById(data.orderId())).thenReturn(Optional.of(order));
        EmailData emailData = EmailData.builder()
                .recipientEmail(data.client().email())
                .subject("Order confirmation")
                .message(getEmailMessageForPaidStatus())
                .build();

        eventPorts.sendEmailWithInvoiceEvent(data);

        verify(emailSender).sendEmailWithInvoice(emailData, null, "PL/12/12/2012/1.pdf");
    }

    private String getEmailMessageForPaidStatus() {
        return """
                Dear %s,
                
                We’re pleased to confirm that your order has been placed successfully.
                
                Please find the invoice for your order attached to this message.
                If you have any questions or need further assistance, feel free to reach out to us.
                
                Best regards,
                Online Shop Team
                """.formatted("firstName");
    }

    private String getAddressString(Address address) {
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
