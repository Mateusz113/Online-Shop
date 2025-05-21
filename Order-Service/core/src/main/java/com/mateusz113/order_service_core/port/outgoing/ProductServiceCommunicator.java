package com.mateusz113.order_service_core.port.outgoing;

import java.util.Map;

public interface ProductServiceCommunicator {
    boolean checkProductsStock(Map<Long, Integer> productsMap);

    void updateSoldProductsStock(Map<Long, Integer> soldProductsMap);
}
