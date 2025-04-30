package com.mateusz113.product_service_core.ports.incoming;

import com.mateusz113.product_service_model.product.Product;

import java.util.List;

public interface AddNewProducts {
    List<Product> add(List<Product> products);
}
