package com.mateusz113.product_service_core.ports.incoming;

import java.util.Map;

public interface CheckProductsStock {
    void checkStock(Map<Long, Integer> productsStockMap);
}
