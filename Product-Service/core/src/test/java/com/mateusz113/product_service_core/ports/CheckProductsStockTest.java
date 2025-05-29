package com.mateusz113.product_service_core.ports;

import com.mateusz113.product_service_core.ProductServiceFacade;
import com.mateusz113.product_service_core.ProductServiceVerifier;
import com.mateusz113.product_service_core.ports.incoming.CheckProductsStock;
import com.mateusz113.product_service_core.ports.outgoing.ProductServiceDatabase;
import com.mateusz113.product_service_model.exception.InvalidProductStockException;
import com.mateusz113.product_service_model.exception.ProductDoesNotExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.mateusz113.product_service_core.util.ProductServiceCoreTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CheckProductsStockTest {
    private ProductServiceDatabase database;
    private CheckProductsStock checkProductsStock;

    @BeforeEach
    void setUp() {
        Clock clock = getClock();
        database = mock(ProductServiceDatabase.class);
        ProductServiceVerifier verifier = new ProductServiceVerifier(clock);
        checkProductsStock = new ProductServiceFacade(database, verifier, clock);
    }

    @Test
    void checkStock_IncorrectIdProvided_ThrowsProductDoesNotExistException() {
        Long productId = 1L;
        Integer requiredStock = 5;
        when(database.findById(productId)).thenReturn(Optional.empty());

        ProductDoesNotExistException exception = assertThrows(ProductDoesNotExistException.class, () -> checkProductsStock.checkStock(productId, requiredStock));

        assertEquals("Product with given ID does not exist.", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void checkStock_UnavailableRequiredStockProvided_ThrowsInvalidProductStockException() {
        Long productId = 1L;
        Integer requiredStock = 500;
        when(database.findById(productId)).thenReturn(Optional.of(getProduct()));

        InvalidProductStockException exception = assertThrows(InvalidProductStockException.class, () -> checkProductsStock.checkStock(productId, requiredStock));

        assertEquals("The current product (id: 1) stock (current stock: 10) is lower than required (required: 500).", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void checkStock_CorrectDataProvided_DoesNotThrowAnything() {
        Long productId = 1L;
        Integer requiredStock = 5;
        when(database.findById(productId)).thenReturn(Optional.of(getProduct()));

        assertDoesNotThrow(() -> checkProductsStock.checkStock(productId, requiredStock));
    }

}
