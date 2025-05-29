package com.mateusz113.product_service_core.ports;

import com.mateusz113.product_service_core.ProductServiceFacade;
import com.mateusz113.product_service_core.ProductServiceVerifier;
import com.mateusz113.product_service_core.ports.incoming.AddNewProducts;
import com.mateusz113.product_service_core.ports.outgoing.ProductServiceDatabase;
import com.mateusz113.product_service_model.customization.CustomizationElement;
import com.mateusz113.product_service_model.customization.CustomizationOption;
import com.mateusz113.product_service_model.product.Product;
import com.mateusz113.product_service_model.product.ProductDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.List;

import static com.mateusz113.product_service_core.util.ProductServiceCoreTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AddNewProductsTest {
    private ProductServiceDatabase database;
    private AddNewProducts addNewProducts;

    @BeforeEach
    void setUp() {
        Clock clock = getClock();
        database = mock(ProductServiceDatabase.class);
        ProductServiceVerifier verifier = new ProductServiceVerifier(clock);
        addNewProducts = new ProductServiceFacade(database, verifier, clock);
    }

    @Test
    void add_CorrectDataIsPassed_ReturnsProductsList() {
        List<Product> products = List.of(getProduct(), getProduct());
        when(database.save(getProduct())).thenReturn(getProduct());

        List<Product> resultList = addNewProducts.add(products);

        verify(database, times(products.size())).save(getProduct());
        for (Product result : resultList) {
            assertEquals(1, result.getId());
            assertEquals("name", result.getName());
            assertEquals("brand", result.getBrand());
            assertEquals(getDefaultPrice(), result.getPrice());
            assertEquals("type", result.getType());
            assertEquals(getDefaultAvailableAmount(), result.getAvailableAmount());
            for (ProductDetail detail : result.getDetails()) {
                assertEquals(1, detail.getId());
                assertEquals("label", detail.getLabel());
                assertEquals("description", detail.getDescription());
            }
            for (CustomizationElement customization : result.getCustomizations()) {
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
