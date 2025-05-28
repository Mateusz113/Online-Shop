package com.mateusz113.order_service_core.facade;

import com.mateusz113.order_service_core.port.incoming.OrderServiceActionPorts;
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
import com.mateusz113.order_service_model.product.Product;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class OrderServiceActionFacade implements OrderServiceActionPorts {
    private final CartServiceCommunicator cartServiceCommunicator;
    private final ProductServiceCommunicator productServiceCommunicator;
    private final OrderServiceDatabase database;
    private final OrderServiceVerifier verifier;
    private final Clock clock;

    @Override
    public Order place(Order order) {
        verifier.verifyOrderData(order);
        updateOrderData(order);
        checkProductsAvailability(order.getProducts());
        return database.save(order);
    }

    @Override
    public Order getById(Long orderId) {
        return getOrderById(orderId);
    }

    @Override
    public PageableContent<Order> getClientOrders(Long clientId, Integer pageNumber, Integer pageSize) {
        return database.findByClientId(clientId, pageNumber, pageSize);
    }

    @Override
    public PageableContent<Order> getAllOrders(Integer pageNumber, Integer pageSize) {
        return database.findAll(pageNumber, pageSize);
    }

    @Override
    public byte[] getOrderInvoice(Long orderId) {
        Order order = getOrderById(orderId);
        if (order.getOrderStatus() == OrderStatus.PENDING) {
            throw new OrderNotPaidException("There is no invoice for unpaid orders.", OffsetDateTime.now(clock));
        }
        if (isNull(order.getInvoice())) {
            throw new InvoiceNotGeneratedException("The invoice is not present for this order.", OffsetDateTime.now(clock));
        }
        return order.getInvoice();
    }

    @Override
    public void delete(Long orderId) {
        Order order = getOrderById(orderId);
        database.delete(order);
    }

    private Order getOrderById(Long orderId) {
        return database.findById(orderId)
                .orElseThrow(() -> new OrderDoesNotExistException("Order with id: %d does not exist.".formatted(orderId), OffsetDateTime.now(clock)));
    }

    private void updateOrderData(Order order) {
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPlacementTime(OffsetDateTime.now(clock));
        List<Product> products = cartServiceCommunicator.getProductsData(order.getCartId());
        order.setProducts(products);
    }

    private void checkProductsAvailability(List<Product> products) {
        Map<Long, Integer> orderProductsData = products.stream()
                .collect(Collectors.toMap(Product::getSourceId, Product::getQuantity, Integer::sum));
        if (!productServiceCommunicator.areProductsInStock(orderProductsData)) {
            throw new ProductNotInStockException("Cannot create an order for items requested amounts, as they are not in stock.", OffsetDateTime.now(clock));
        }
    }
}
