package com.mateusz113.order_service_core.port.incoming;

import com.mateusz113.order_service_core.facade.OrderServiceActionFacade;
import com.mateusz113.order_service_core.port.outgoing.CartServiceCommunicator;
import com.mateusz113.order_service_core.port.outgoing.OrderServiceDatabase;
import com.mateusz113.order_service_core.port.outgoing.ProductServiceCommunicator;
import com.mateusz113.order_service_core.verifier.OrderServiceVerifier;
import com.mateusz113.order_service_model.content.PageableContent;
import com.mateusz113.order_service_model.exception.InvoiceNotGeneratedException;
import com.mateusz113.order_service_model.exception.OrderDoesNotExistException;
import com.mateusz113.order_service_model.exception.OrderNotPaidException;
import com.mateusz113.order_service_model.exception.ProductNotInStockException;
import com.mateusz113.order_service_model.order.Order;
import com.mateusz113.order_service_model.order.OrderStatus;
import com.mateusz113.order_service_model.product.Customization;
import com.mateusz113.order_service_model.product.CustomizationOption;
import com.mateusz113.order_service_model.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.mateusz113.order_service_core.util.OrderServiceCoreUtil.getClock;
import static com.mateusz113.order_service_core.util.OrderServiceCoreUtil.getDefaultPrice;
import static com.mateusz113.order_service_core.util.OrderServiceCoreUtil.getDefaultPriceDifference;
import static com.mateusz113.order_service_core.util.OrderServiceCoreUtil.getDefaultQuantity;
import static com.mateusz113.order_service_core.util.OrderServiceCoreUtil.getDefaultTime;
import static com.mateusz113.order_service_core.util.OrderServiceCoreUtil.getOrder;
import static com.mateusz113.order_service_core.util.OrderServiceCoreUtil.getPageableContent;
import static com.mateusz113.order_service_core.util.OrderServiceCoreUtil.getProduct;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OrderServiceActionPortsTest {
    private OrderServiceActionPorts actionPorts;
    private CartServiceCommunicator cartCommunicator;
    private ProductServiceCommunicator productCommunicator;
    private OrderServiceDatabase database;

    @BeforeEach
    void setUp() {
        Clock clock = getClock();
        cartCommunicator = mock(CartServiceCommunicator.class);
        productCommunicator = mock(ProductServiceCommunicator.class);
        database = mock(OrderServiceDatabase.class);
        OrderServiceVerifier verifier = new OrderServiceVerifier(clock);
        actionPorts = new OrderServiceActionFacade(cartCommunicator, productCommunicator, database, verifier, clock);
    }

    @Test
    void place_ProductsAreNotAvailable_ThrowsProductNotInStockException() {
        Order order = getOrder();
        when(cartCommunicator.getProductsData(order.getCartId())).thenReturn(List.of(getProduct(), getProduct()));
        when(productCommunicator.areProductsInStock(Map.of(1L, 20))).thenReturn(false);

        ProductNotInStockException exception = assertThrows(ProductNotInStockException.class, () -> actionPorts.place(order));

        assertEquals("Cannot create an order for items requested amounts, as they are not in stock.", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void place_DataIsCorrect_SavesAndReturnsOrder() {
        Order order = getOrder();
        when(cartCommunicator.getProductsData(order.getCartId())).thenReturn(List.of(getProduct(), getProduct()));
        when(productCommunicator.areProductsInStock(Map.of(1L, 20))).thenReturn(true);
        when(database.save(order)).thenReturn(order);

        Order result = actionPorts.place(order);

        verify(database).save(order);
        verifyOrder(result);
    }

    @Test
    void getById_OrderWithIdNotFound_ThrowsOrderDoesNotExistException() {
        Long orderId = 1L;
        when(database.findById(orderId)).thenReturn(Optional.empty());

        OrderDoesNotExistException exception = assertThrows(OrderDoesNotExistException.class, () -> actionPorts.getById(orderId));

        assertEquals("Order with id: 1 does not exist.", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void getById_DataIsCorrect_ReturnsOrder() {
        Long orderId = 1L;
        when(database.findById(orderId)).thenReturn(Optional.of(getOrder()));

        Order result = actionPorts.getById(orderId);

        verifyOrder(result);
    }

    @Test
    void getClientOrders_DataIsCorrect_ReturnsPageableContentOfClientOrders() {
        Long clientId = 1L;
        int pageNumber = 0;
        int pageSize = 2;
        when(database.findByClientId(clientId, pageNumber, pageSize)).thenReturn(getPageableContent(pageNumber, pageSize));

        PageableContent<Order> result = actionPorts.getClientOrders(clientId, pageNumber, pageSize);

        assertEquals(1, result.totalPages());
        assertEquals(2L, result.totalElements());
        assertEquals(pageNumber, result.pageNumber());
        assertEquals(pageSize, result.pageSize());
        result.elements().forEach(this::verifyOrder);
    }

    @Test
    void getAllOrders_DataIsCorrect_ReturnsPageableContentOfOrder() {
        int pageNumber = 0;
        int pageSize = 2;
        when(database.findAll(pageNumber, pageSize)).thenReturn(getPageableContent(pageNumber, pageSize));

        PageableContent<Order> result = actionPorts.getAllOrders(pageNumber, pageSize);

        assertEquals(1, result.totalPages());
        assertEquals(2L, result.totalElements());
        assertEquals(pageNumber, result.pageNumber());
        assertEquals(pageSize, result.pageSize());
        result.elements().forEach(this::verifyOrder);
    }

    @Test
    void getOrderInvoice_OrderWithIdNotFound_ThrowsOrderDoesNotExistException() {
        Long orderId = 1L;
        when(database.findById(orderId)).thenReturn(Optional.empty());

        OrderDoesNotExistException exception = assertThrows(OrderDoesNotExistException.class, () -> actionPorts.getOrderInvoice(orderId));

        assertEquals("Order with id: 1 does not exist.", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void getOrderInvoice_OrderStatusIsPending_ThrowsOrderNotPaidException() {
        Long orderId = 1L;
        when(database.findById(orderId)).thenReturn(Optional.of(getOrder()));

        OrderNotPaidException exception = assertThrows(OrderNotPaidException.class, () -> actionPorts.getOrderInvoice(orderId));

        assertEquals("There is no invoice for unpaid orders.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void getOrderInvoice_OrderInvoiceIsNotPresent_ThrowsOrderNotPaidException() {
        Long orderId = 1L;
        Order order = getOrder();
        order.setOrderStatus(OrderStatus.PAID);
        when(database.findById(orderId)).thenReturn(Optional.of(order));

        InvoiceNotGeneratedException exception = assertThrows(InvoiceNotGeneratedException.class, () -> actionPorts.getOrderInvoice(orderId));

        assertEquals("The invoice is not present for this order.", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void getOrderInvoice_DataIsCorrect_ReturnsInvoice() {
        Long orderId = 1L;
        Order order = getOrder();
        order.setOrderStatus(OrderStatus.PAID);
        byte[] invoice = new byte[0];
        order.setInvoice(invoice);
        when(database.findById(orderId)).thenReturn(Optional.of(order));

        byte[] result = actionPorts.getOrderInvoice(orderId);

        assertArrayEquals(invoice, result);
    }

    @Test
    void delete_OrderWithIdNotFound_ThrowsOrderDoesNotExistException() {
        Long orderId = 1L;
        when(database.findById(orderId)).thenReturn(Optional.empty());

        OrderDoesNotExistException exception = assertThrows(OrderDoesNotExistException.class, () -> actionPorts.delete(orderId));

        assertEquals("Order with id: 1 does not exist.", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void delete_DataIsCorrect_DeletesOrder() {
        Long orderId = 1L;
        Order order = getOrder();
        when(database.findById(orderId)).thenReturn(Optional.of(order));

        actionPorts.delete(orderId);

        verify(database).delete(order);
    }

    private void verifyOrder(Order order) {
        assertEquals(1L, order.getId());
        assertEquals(1L, order.getCartId());
        assertEquals(1L, order.getClientId());
        assertEquals("clientComment", order.getClientComment());
        assertEquals(getDefaultTime(), order.getPlacementTime());
        assertEquals(OrderStatus.PENDING, order.getOrderStatus());
        assertNull(order.getInvoice());
        for (Product product : order.getProducts()) {
            assertEquals(1L, product.getId());
            assertEquals(1L, product.getSourceId());
            assertEquals("name", product.getName());
            assertEquals("brand", product.getBrand());
            assertEquals(getDefaultPrice(), product.getPrice());
            assertEquals(getDefaultQuantity(), product.getQuantity());
            for (Customization customization : product.getCustomizations()) {
                assertEquals(1L, customization.getId());
                assertEquals("name", customization.getName());
                assertEquals(true, customization.getMultipleChoice());
                for (CustomizationOption option : customization.getOptions()) {
                    assertEquals(1L, option.getId());
                    assertEquals("name", option.getName());
                    assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
                }
            }
        }
    }
}
