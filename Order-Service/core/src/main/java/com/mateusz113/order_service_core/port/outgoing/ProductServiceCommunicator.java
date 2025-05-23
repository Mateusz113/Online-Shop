package com.mateusz113.order_service_core.port.outgoing;

import java.util.Map;

public interface ProductServiceCommunicator {
    boolean areProductsInStock(Map<Long, Integer> productsMap);

    void updateSoldProductsStock(Map<Long, Integer> soldProductsMap);
}
