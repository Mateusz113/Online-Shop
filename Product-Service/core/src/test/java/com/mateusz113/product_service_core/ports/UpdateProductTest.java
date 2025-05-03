package com.mateusz113.product_service_core.ports;

import com.mateusz113.product_service_core.ProductServiceFacade;
import com.mateusz113.product_service_core.ProductServiceVerifier;
import com.mateusz113.product_service_core.argument_matcher.ProductArgumentMatcher;
import com.mateusz113.product_service_core.ports.incoming.UpdateProduct;
import com.mateusz113.product_service_core.ports.outgoing.ProductServiceDatabase;
import com.mateusz113.product_service_model.exception.ProductDoesNotExistException;
import com.mateusz113.product_service_model.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import java.time.Clock;
import java.util.Optional;

import static com.mateusz113.product_service_core.util.ProductServiceCoreTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UpdateProductTest {
    private ProductServiceDatabase database;
    private UpdateProduct updateProduct;

    @BeforeEach
    void setUp() {
        Clock clock = getClock();
        database = mock(ProductServiceDatabase.class);
        ProductServiceVerifier verifier = new ProductServiceVerifier(clock);
        updateProduct = new ProductServiceFacade(database, verifier, clock);
    }

    @Test
    void update_IdIsNotCorrect_ThrowsProductDoesNotExistException() {
        Long id = 1L;
        Product newData = getProduct();
        when(database.findById(id)).thenReturn(Optional.empty());

        ProductDoesNotExistException exception = assertThrows(ProductDoesNotExistException.class, () -> updateProduct.update(newData, id));

        assertEquals("Product with given ID does not exist.", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void update_IdIsCorrect_UpdatesProduct() {
        Long id = 1L;
        Product newData = getProduct();
        ArgumentMatcher<Product> productArgumentMatcher = new ProductArgumentMatcher(newData);
        when(database.findById(id)).thenReturn(Optional.of(Product.builder().id(1L).build()));

        updateProduct.update(newData, id);

        verify(database, times(1)).save(argThat(productArgumentMatcher));
    }
}
