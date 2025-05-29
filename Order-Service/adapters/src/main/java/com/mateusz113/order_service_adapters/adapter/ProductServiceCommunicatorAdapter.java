package com.mateusz113.order_service_adapters.adapter;


import com.mateusz113.order_service_adapters.client.ProductServiceClient;
import com.mateusz113.order_service_core.port.outgoing.ProductServiceCommunicator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductServiceCommunicatorAdapter implements ProductServiceCommunicator {
    private final ProductServiceClient productClient;

    @Override
    public boolean areProductsInStock(Map<Long, Integer> productsMap) {
        for (Map.Entry<Long, Integer> mapEntry : productsMap.entrySet()) {
            ResponseEntity<Void> response = productClient.checkProductsAvailability(mapEntry.getKey(), mapEntry.getValue());
            if (response.getStatusCode() == HttpStatus.CONFLICT) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void updateSoldProductsStock(Map<Long, Integer> soldProductsMap) {
        productClient.updateSoldProductsStock(soldProductsMap);
    }
}
