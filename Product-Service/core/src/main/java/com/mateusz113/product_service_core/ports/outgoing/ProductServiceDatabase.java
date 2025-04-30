package com.mateusz113.product_service_core.ports.outgoing;

import com.mateusz113.product_service_model.content_management.PageableContent;
import com.mateusz113.product_service_model.product.Product;

import java.util.Optional;

public interface ProductServiceDatabase {
    Product save(Product product);

    void delete(Product product);

    Optional<Product> findById(Long productId);

    PageableContent<Product> findAll(int pageNumber, int pageSize);

    PageableContent<Product> findByType(String type, int pageNumber, int pageSize);
}
