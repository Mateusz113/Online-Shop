package com.mateusz113.product_service_adapters.specification;

import com.mateusz113.product_service_core.ports.outgoing.ProductServiceDatabase;
import com.mateusz113.product_service_model.filter.ProductFilter;
import com.mateusz113.product_service_model.product.Product;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static com.mateusz113.product_service_adapters.util.ProductServiceAdaptersTestUtil.getProduct;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class ProductSpecificationTest {
    @Autowired
    private ProductServiceDatabase database;

    @BeforeEach
    void setUp() {
        database.save(getProduct("Gaming Laptop", "A", new BigDecimal(500), "Laptop", 10));
        database.save(getProduct("Coffee Maker", "B", new BigDecimal(400), "Electronic", 20));
        database.save(getProduct("Pear Phone", "C", new BigDecimal(300), "Phone", 30));
        database.save(getProduct("Gamepad", "D", new BigDecimal(200), "Electronic", 40));
    }

    @Test
    void getSpecificationFromFilter_NameIsProvided_ReturnsCorrectProductList() {
        Pageable pageable = PageRequest.of(0, 50);
        String name = "Gaming";
        ProductFilter filter = ProductFilter.builder()
                .name(name)
                .build();

        List<Product> results = database.findAll(filter, pageable.getPageNumber(), pageable.getPageSize()).elements();

        assertEquals(1, results.size());
        for (Product result : results) {
            assertTrue(result.getName().contains(name));
        }
    }

    @Test
    void getSpecificationFromFilter_BrandIsProvided_ReturnsCorrectProductList() {
        Pageable pageable = PageRequest.of(0, 50);
        String brand = "A";
        ProductFilter filter = ProductFilter.builder()
                .brand(brand)
                .build();

        List<Product> results = database.findAll(filter, pageable.getPageNumber(), pageable.getPageSize()).elements();

        assertEquals(1, results.size());
        for (Product result : results) {
            assertTrue(result.getBrand().contains(brand));
        }
    }

    @Test
    void getSpecificationFromFilter_MinPriceIsProvided_ReturnsCorrectProductList() {
        Pageable pageable = PageRequest.of(0, 50);
        ProductFilter filter = ProductFilter.builder()
                .minPrice(new BigDecimal(300))
                .build();

        List<Product> results = database.findAll(filter, pageable.getPageNumber(), pageable.getPageSize()).elements();

        assertEquals(3, results.size());
        for (Product result : results) {
            assertTrue(result.getPrice().compareTo(BigDecimal.valueOf(300)) >= 0);
        }
    }

    @Test
    void getSpecificationFromFilter_MaxPriceIsProvided_ReturnsCorrectProductList() {
        Pageable pageable = PageRequest.of(0, 50);
        ProductFilter filter = ProductFilter.builder()
                .maxPrice(new BigDecimal(300))
                .build();

        List<Product> results = database.findAll(filter, pageable.getPageNumber(), pageable.getPageSize()).elements();

        assertEquals(2, results.size());
        for (Product result : results) {
            assertTrue(result.getPrice().compareTo(BigDecimal.valueOf(300)) <= 0);
        }
    }

    @Test
    void getSpecificationFromFilter_TypeIsProvided_ReturnsCorrectProductList() {
        Pageable pageable = PageRequest.of(0, 50);
        String type = "Electronic";
        ProductFilter filter = ProductFilter.builder()
                .type(type)
                .build();

        List<Product> results = database.findAll(filter, pageable.getPageNumber(), pageable.getPageSize()).elements();

        assertEquals(2, results.size());
        for (Product result : results) {
            assertEquals(type, result.getType());
        }
    }

    @Test
    void getSpecificationFromFilter_MinAvailableAmountIsProvided_ReturnsCorrectProductList() {
        Pageable pageable = PageRequest.of(0, 50);
        int minAvailableAmount = 20;
        ProductFilter filter = ProductFilter.builder()
                .minAvailableAmount(minAvailableAmount)
                .build();

        List<Product> results = database.findAll(filter, pageable.getPageNumber(), pageable.getPageSize()).elements();

        assertEquals(3, results.size());
        for (Product result : results) {
            assertTrue(result.getAvailableAmount() >= minAvailableAmount);
        }
    }
}
