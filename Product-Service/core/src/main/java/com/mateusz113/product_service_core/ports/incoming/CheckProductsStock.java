package com.mateusz113.product_service_core.ports.incoming;

public interface CheckProductsStock {
    void checkStock(Long productId, Integer requiredStock);
}
