package com.mateusz113.product_service_core.ports.outgoing;

import com.mateusz113.product_service_model.content_management.PageableContent;
import com.mateusz113.product_service_model.filter.ProductFilter;
import com.mateusz113.product_service_model.product.Product;

import java.util.List;
import java.util.Optional;

public interface ProductServiceDatabase {
    Product save(Product product);

    void saveAll(List<Product> products);

    Optional<Product> findById(Long productId);

    List<Product> findAllByIds(List<Long> ids);

    PageableContent<Product> findAll(ProductFilter filter, int pageNumber, int pageSize);

    void delete(Product product);
}
