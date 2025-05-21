package com.mateusz113.order_service_core.port.incoming;

import com.mateusz113.order_service_model.client.Client;
import com.mateusz113.order_service_model.content.PageableContent;
import com.mateusz113.order_service_model.order.Order;

public interface OrderServiceActionPorts {
    Order place(Order order);

    PageableContent<Order> getClientOrders(Long clientId, Integer pageNumber, Integer pageSize);

    Order getById(Long orderId);

    PageableContent<Order> getAllOrders(Integer pageNumber, Integer pageSize);

    byte[] getOrderInvoice(Long orderId);

    void delete(Long orderId);
}
