package com.mateusz113.order_service_core.port.outgoing;

import com.mateusz113.order_service_model.product.Product;

import java.util.List;

public interface CartServiceCommunicator {
    List<Product> getProductsData(Long cartId);

    void deleteCart(Long cartId);
}
