package com.mateusz113.product_service_core.ports;

import com.mateusz113.product_service_core.ProductServiceFacade;
import com.mateusz113.product_service_core.ProductServiceVerifier;
import com.mateusz113.product_service_core.ports.incoming.UpdateProductsStock;
import com.mateusz113.product_service_core.ports.outgoing.ProductServiceDatabase;
import com.mateusz113.product_service_model.exception.InvalidProductStockException;
import com.mateusz113.product_service_model.exception.ProductDoesNotExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.List;
import java.util.Map;

import static com.mateusz113.product_service_core.util.ProductServiceCoreTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UpdateProductsStockTest {
    private ProductServiceDatabase database;
    private UpdateProductsStock updateProductsStock;

    @BeforeEach
    void setUp() {
        Clock clock = getClock();
        database = mock(ProductServiceDatabase.class);
        ProductServiceVerifier verifier = new ProductServiceVerifier(clock);
        updateProductsStock = new ProductServiceFacade(database, verifier, clock);
    }

    @Test
    void checkStock_IncorrectIdsProvided_ThrowsProductDoesNotExistException() {
        Map<Long, Integer> productsStockMap = Map.of(1L, 1);
        when(database.findAllByIds(List.of(1L))).thenReturn(List.of());

        ProductDoesNotExistException exception = assertThrows(ProductDoesNotExistException.class, () -> updateProductsStock.updateStock(productsStockMap));

        assertEquals("Not all ids are viable product ids.", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void checkStock_UnavailableAmountsProvided_ThrowsInvalidProductStockException() {
        Map<Long, Integer> productsStockMap = Map.of(1L, 500);
        when(database.findAllByIds(List.of(1L))).thenReturn(List.of(getProduct()));

        InvalidProductStockException exception = assertThrows(InvalidProductStockException.class, () -> updateProductsStock.updateStock(productsStockMap));

        assertEquals("The current product (id: 1) stock (current stock: 10) is lower than sold amount (required: 500).", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void checkStock_CorrectDataProvided_DoesNotThrowAnything() {
        Map<Long, Integer> productsStockMap = Map.of(1L, 5);
        when(database.findAllByIds(List.of(1L))).thenReturn(List.of(getProduct()));

        assertDoesNotThrow(() -> updateProductsStock.updateStock(productsStockMap));
    }
}
