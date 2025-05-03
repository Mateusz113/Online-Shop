package com.mateusz113.product_service_core.ports;

import com.mateusz113.product_service_core.ProductServiceFacade;
import com.mateusz113.product_service_core.ProductServiceVerifier;
import com.mateusz113.product_service_core.ports.incoming.GetDetailedProduct;
import com.mateusz113.product_service_core.ports.outgoing.ProductServiceDatabase;
import com.mateusz113.product_service_model.customization.CustomizationElement;
import com.mateusz113.product_service_model.customization.CustomizationOption;
import com.mateusz113.product_service_model.exception.ProductDoesNotExistException;
import com.mateusz113.product_service_model.product.Product;
import com.mateusz113.product_service_model.product.ProductDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.Optional;

import static com.mateusz113.product_service_core.util.ProductServiceCoreTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetDetailedProductTest {
    private ProductServiceDatabase database;
    private GetDetailedProduct getDetailedProduct;

    @BeforeEach
    void setUp() {
        Clock clock = getClock();
        database = mock(ProductServiceDatabase.class);
        ProductServiceVerifier verifier = new ProductServiceVerifier(clock);
        getDetailedProduct = new ProductServiceFacade(database, verifier, clock);
    }

    @Test
    void getById_IdIsIncorrect_ThrowsProductDoesNotExistException() {
        Long id = 1L;
        when(database.findById(id)).thenReturn(Optional.empty());

        ProductDoesNotExistException exception = assertThrows(ProductDoesNotExistException.class, () -> getDetailedProduct.getById(id));

        assertEquals("Product with given ID does not exist.", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void getById_IdIsCorrect_ReturnsProduct() {
        Product product = getProduct();
        Long id = 1L;
        when(database.findById(id)).thenReturn(Optional.of(product));

        Product result = getDetailedProduct.getById(id);

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
