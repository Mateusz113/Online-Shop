package com.mateusz113.cart_service_core.ports.incoming;

import com.mateusz113.cart_service_model.product.CustomizedProduct;

public interface AddProduct {
    void add(CustomizedProduct customizedProduct, Long cartId);
}
