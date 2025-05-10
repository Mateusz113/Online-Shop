package com.mateusz113.cart_service_core.ports.outgoing;

import com.mateusz113.cart_service_model.product.SourceProduct;

public interface ProductServiceCommunicator {
    SourceProduct getProductSourceData(Long id);
}
