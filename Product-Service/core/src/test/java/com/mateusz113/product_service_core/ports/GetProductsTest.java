package com.mateusz113.product_service_core.ports;

import com.mateusz113.product_service_core.ProductServiceFacade;
import com.mateusz113.product_service_core.ProductServiceVerifier;
import com.mateusz113.product_service_core.ports.incoming.GetProducts;
import com.mateusz113.product_service_core.ports.outgoing.ProductServiceDatabase;
import com.mateusz113.product_service_model.content_management.PageableContent;
import com.mateusz113.product_service_model.customization.CustomizationElement;
import com.mateusz113.product_service_model.customization.CustomizationOption;
import com.mateusz113.product_service_model.filter.ProductFilter;
import com.mateusz113.product_service_model.product.Product;
import com.mateusz113.product_service_model.product.ProductDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static com.mateusz113.product_service_core.util.ProductServiceCoreTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetProductsTest {
    private ProductServiceDatabase database;
    private GetProducts getProducts;

    @BeforeEach
    void setUp() {
        Clock clock = getClock();
        database = mock(ProductServiceDatabase.class);
        ProductServiceVerifier verifier = new ProductServiceVerifier(clock);
        getProducts = new ProductServiceFacade(database, verifier, clock);
    }

    @Test
    void getAll_CorrectDataIsPassed_ReturnsPageableContent() {
        ProductFilter filter = ProductFilter.builder().build();
        int pageNumber = 0;
        int pageSize = 3;
        when(database.findAll(filter, pageNumber, pageSize)).thenReturn(getProductPageableContent(pageNumber, pageSize));

        PageableContent<Product> result = getProducts.getAll(filter, pageNumber, pageSize);

        assertEquals(1, result.totalPages());
        assertEquals(2, result.totalElements());
        assertEquals(pageNumber, result.pageNumber());
        assertEquals(pageSize, result.pageSize());
        for (Product product : result.elements()) {
            assertEquals(1, product.getId());
            assertEquals("name", product.getName());
            assertEquals("brand", product.getBrand());
            assertEquals(getDefaultPrice(), product.getPrice());
            assertEquals("type", product.getType());
            assertEquals(getDefaultAvailableAmount(), product.getAvailableAmount());
            for (ProductDetail detail : product.getDetails()) {
                assertEquals(1, detail.getId());
                assertEquals("label", detail.getLabel());
                assertEquals("description", detail.getDescription());
                assertNull(detail.getProduct());
            }
            for (CustomizationElement customization : product.getCustomizations()) {
                assertEquals(1, customization.getId());
                assertEquals("name", customization.getName());
                assertTrue(customization.getMultipleChoice());
                assertNull(customization.getProduct());
                for (CustomizationOption option : customization.getOptions()) {
                    assertEquals(1, option.getId());
                    assertEquals("name", option.getName());
                    assertFalse(option.getDefaultOption());
                    assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
                    assertNull(option.getCustomizationElement());
                }
            }
        }
    }
}
