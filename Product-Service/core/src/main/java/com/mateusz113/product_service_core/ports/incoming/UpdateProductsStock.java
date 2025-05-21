package com.mateusz113.product_service_core.ports.incoming;

import java.util.Map;

public interface UpdateProductsStock {
    void updateStock(Map<Long, Integer> productsStockMap);
}
