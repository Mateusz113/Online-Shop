package com.mateusz113.product_service_core.ports;

import com.mateusz113.product_service_core.ProductServiceFacade;
import com.mateusz113.product_service_core.ProductServiceVerifier;
import com.mateusz113.product_service_core.ports.incoming.DeleteProduct;
import com.mateusz113.product_service_core.ports.outgoing.ProductServiceDatabase;
import com.mateusz113.product_service_model.exception.ProductDoesNotExistException;
import com.mateusz113.product_service_model.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.Optional;

import static com.mateusz113.product_service_core.util.ProductServiceCoreTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DeleteProductTest {
    private ProductServiceDatabase database;
    private DeleteProduct deleteProduct;

    @BeforeEach
    void setUp() {
        Clock clock = getClock();
        database = mock(ProductServiceDatabase.class);
        ProductServiceVerifier verifier = new ProductServiceVerifier(clock);
        deleteProduct = new ProductServiceFacade(database, verifier, clock);
    }

    @Test
    void delete_IdIsNotCorrect_ThrowsProductDoesNotExistException() {
        Long id = 1L;
        when(database.findById(id)).thenReturn(Optional.empty());

        ProductDoesNotExistException exception = assertThrows(ProductDoesNotExistException.class, () -> deleteProduct.delete(id));

        assertEquals("Product with given ID does not exist.", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void delete_IdIsCorrect_DeletesProduct() {
        Long id = 1L;
        Product product = getProduct();
        when(database.findById(id)).thenReturn(Optional.of(product));

        deleteProduct.delete(id);

        verify(database, times(1)).delete(product);
    }
}
