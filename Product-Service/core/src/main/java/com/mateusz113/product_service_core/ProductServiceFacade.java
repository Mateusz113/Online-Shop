package com.mateusz113.product_service_core;

import com.mateusz113.product_service_core.ports.incoming.AddNewProducts;
import com.mateusz113.product_service_core.ports.incoming.DeleteProduct;
import com.mateusz113.product_service_core.ports.incoming.GetDetailedProduct;
import com.mateusz113.product_service_core.ports.incoming.GetProducts;
import com.mateusz113.product_service_core.ports.incoming.UpdateProduct;
import com.mateusz113.product_service_core.ports.outgoing.ProductServiceDatabase;
import com.mateusz113.product_service_model.content_management.PageableContent;
import com.mateusz113.product_service_model.exception.ProductDoesNotExistException;
import com.mateusz113.product_service_model.filter.ProductFilter;
import com.mateusz113.product_service_model.product.Product;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ProductServiceFacade implements AddNewProducts, DeleteProduct, GetDetailedProduct, GetProducts, UpdateProduct {
    private final ProductServiceDatabase database;
    private final ProductServiceVerifier verifier;
    private final Clock clock;

    @Override
    public List<Product> add(List<Product> products) {
        verifier.verifyProductList(products);
        List<Product> savedProducts = new ArrayList<>();
        for (Product product : products) {
            savedProducts.add(database.save(product));
        }
        return savedProducts;
    }

    @Override
    public Product getById(Long productId) {
        verifier.verifyProductId(productId);
        return findProductById(productId);
    }

    @Override
    public PageableContent<Product> getAll(ProductFilter filter, int pageNumber, int pageSize) {
        verifier.verifyProductFilter(filter);
        return database.findAll(filter, pageNumber, pageSize);
    }

    @Override
    public void update(Product newProductData, Long productId) {
        verifier.verifyProduct(newProductData);
        Product product = findProductById(productId);
        product.update(newProductData);
        database.save(product);
    }

    @Override
    public void delete(Long productId) {
        verifier.verifyProductId(productId);
        Product product = findProductById(productId);
        database.delete(product);
    }

    private Product findProductById(Long productId) {
        return database.findById(productId)
                .orElseThrow(() -> new ProductDoesNotExistException("Product with given ID does not exist.", OffsetDateTime.now(clock)));
    }
}
