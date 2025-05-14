package com.mateusz113.cart_service_adapters.adapter;

import com.mateusz113.cart_service_adapters.client.ProductServiceClient;
import com.mateusz113.cart_service_core.ports.outgoing.ProductServiceCommunicator;
import com.mateusz113.cart_service_model.product.SourceProduct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProductServiceCommunicatorAdapter implements ProductServiceCommunicator {
    private final ProductServiceClient productServiceClient;

    @Override
    public SourceProduct getProductSourceData(Long id) {
        return productServiceClient.getProductById(id);
    }
}
