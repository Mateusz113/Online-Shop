package com.mateusz113.product_service_core.ports.incoming;

import com.mateusz113.product_service_model.product.Product;

public interface UpdateProduct {
    void update(Product newProductData, Long productId);
}
