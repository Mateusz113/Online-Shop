package com.mateusz113.product_service_core.ports.incoming;

import com.mateusz113.product_service_model.product.Product;

public interface GetDetailedProduct {
    Product getById(Long productId);
}
