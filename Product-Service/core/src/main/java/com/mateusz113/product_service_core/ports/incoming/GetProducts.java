package com.mateusz113.product_service_core.ports.incoming;

import com.mateusz113.product_service_model.content_management.PageableContent;
import com.mateusz113.product_service_model.product.Product;

public interface GetProducts {
    PageableContent<Product> getAll(int pageNumber, int pageSize);

    PageableContent<Product> getByType(String type, int pageNumber, int pageSize);
}
