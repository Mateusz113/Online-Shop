package com.mateusz113.product_service_core;

import com.mateusz113.product_service_core.ports.incoming.AddNewProducts;
import com.mateusz113.product_service_core.ports.incoming.CheckProductsStock;
import com.mateusz113.product_service_core.ports.incoming.DeleteProduct;
import com.mateusz113.product_service_core.ports.incoming.GetDetailedProduct;
import com.mateusz113.product_service_core.ports.incoming.GetProducts;
import com.mateusz113.product_service_core.ports.incoming.UpdateProduct;
import com.mateusz113.product_service_core.ports.incoming.UpdateProductsStock;
import com.mateusz113.product_service_core.ports.outgoing.ProductServiceDatabase;
import com.mateusz113.product_service_model.content_management.PageableContent;
import com.mateusz113.product_service_model.exception.InvalidProductStockException;
import com.mateusz113.product_service_model.exception.ProductDoesNotExistException;
import com.mateusz113.product_service_model.filter.ProductFilter;
import com.mateusz113.product_service_model.product.Product;
import com.mateusz113.product_service_model.util.Pair;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ProductServiceFacade implements AddNewProducts, DeleteProduct, GetDetailedProduct, GetProducts, UpdateProduct, CheckProductsStock, UpdateProductsStock {
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
    public void checkStock(Map<Long, Integer> productsStockMap) {
        Pair<List<Long>, List<Integer>> idsAndQuantities = getIdsAndQuantities(productsStockMap);
        List<Product> products = findProductsByIds(idsAndQuantities.first());
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            Integer requiredQuantity = idsAndQuantities.second().get(i);
            if (product.getAvailableAmount() < requiredQuantity) {
                throw new InvalidProductStockException(
                        "The current product (id: %d) stock (current stock: %d) is lower than required (required: %d).".formatted(
                                product.getAvailableAmount(), product.getAvailableAmount(), requiredQuantity
                        ),
                        OffsetDateTime.now(clock)
                );
            }
        }
    }

    @Override
    public void updateStock(Map<Long, Integer> productsStockMap) {
        Pair<List<Long>, List<Integer>> idsAndQuantities = getIdsAndQuantities(productsStockMap);
        List<Product> products = findProductsByIds(idsAndQuantities.first());
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            Integer quantitySold = idsAndQuantities.second().get(i);
            if (product.getAvailableAmount() < quantitySold) {
                throw new InvalidProductStockException(
                        "The current product (id: %d) stock (current stock: %d) is lower than sold amount (required: %d).".formatted(
                                product.getAvailableAmount(), product.getAvailableAmount(), quantitySold
                        ),
                        OffsetDateTime.now(clock)
                );
            }
            product.setAvailableAmount(product.getAvailableAmount() - quantitySold);
        }
        database.saveAll(products);
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

    private List<Product> findProductsByIds(List<Long> ids) {
        List<Product> products = database.findAllByIds(ids);
        if (products.size() != ids.size()) {
            throw new ProductDoesNotExistException("Not all ids are viable product ids.", OffsetDateTime.now(clock));
        }
        return products;
    }

    private Pair<List<Long>, List<Integer>> getIdsAndQuantities(Map<Long, Integer> productsStockMap) {
        List<Long> ids = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();
        productsStockMap.forEach((key, value) -> {
            ids.add(key);
            quantities.add(value);
        });
        return new Pair<>(ids, quantities);
    }
}
