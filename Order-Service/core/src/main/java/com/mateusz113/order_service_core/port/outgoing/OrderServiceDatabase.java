package com.mateusz113.order_service_core.port.outgoing;

import com.mateusz113.order_service_model.content.PageableContent;
import com.mateusz113.order_service_model.order.Order;

import java.util.Optional;

public interface OrderServiceDatabase {
    Order save(Order order);

    void delete(Order order);

    Optional<Order> findById(Long orderId);

    PageableContent<Order> findAll(Integer pageNumber, Integer pageSize);

    PageableContent<Order> findByClientId(Long clientId, Integer pageNumber, Integer pageSize);
}
