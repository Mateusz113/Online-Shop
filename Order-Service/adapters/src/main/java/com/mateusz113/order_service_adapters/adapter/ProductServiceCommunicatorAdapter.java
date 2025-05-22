package com.mateusz113.order_service_adapters.adapter;


import com.mateusz113.order_service_adapters.client.ProductServiceClient;
import com.mateusz113.order_service_core.port.outgoing.ProductServiceCommunicator;
import feign.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductServiceCommunicatorAdapter implements ProductServiceCommunicator {
    private final ProductServiceClient productClient;

    @Override
    public boolean checkProductsStock(Map<Long, Integer> productsMap) {
        try (Response response = productClient.checkProductsAvailability(productsMap)) {
            return response.status() == 204;
        }
    }

    @Override
    public void updateSoldProductsStock(Map<Long, Integer> soldProductsMap) {
        productClient.updateSoldProductsStock(soldProductsMap);
    }
}
