package com.mateusz113.cart_service_core.ports.incoming;

import com.mateusz113.cart_service_core.CartServiceFacade;
import com.mateusz113.cart_service_core.CartServiceVerifier;
import com.mateusz113.cart_service_core.argument_matcher.CustomizedProductArgumentMatcher;
import com.mateusz113.cart_service_core.ports.outgoing.CartServiceDatabase;
import com.mateusz113.cart_service_core.ports.outgoing.ProductServiceCommunicator;
import com.mateusz113.cart_service_model.exception.CustomizedProductDoesNotExistException;
import com.mateusz113.cart_service_model.product.CustomizedProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import java.time.Clock;
import java.util.Optional;

import static com.mateusz113.cart_service_core.util.CartServiceCoreTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DeleteProductTest {
    private DeleteProduct deleteProduct;
    private CartServiceDatabase database;

    @BeforeEach
    void setUp() {
        database = mock(CartServiceDatabase.class);
        ProductServiceCommunicator communicator = mock(ProductServiceCommunicator.class);
        Clock clock = getClock();
        deleteProduct = new CartServiceFacade(database, communicator, new CartServiceVerifier(clock), clock);
    }

    @Test
    void deleteProduct_ProductWithIdDoesNotExist_ThrowsCustomizedProductDoesNotExistException() {
        Long productId = 2L;
        when(database.findProductById(productId)).thenReturn(Optional.empty());

        CustomizedProductDoesNotExistException exception = assertThrows(CustomizedProductDoesNotExistException.class, () -> deleteProduct.deleteProduct(productId));

        assertEquals("Product with given ID: %d, does not exist".formatted(productId), exception.getMessage());
        assertEquals(404, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void deleteProduct_ProductExists_DeletesProduct() {
        Long productId = 1L;
        ArgumentMatcher<CustomizedProduct> productArgumentMatcher = new CustomizedProductArgumentMatcher(getCustomizedProduct());
        when(database.findProductById(productId)).thenReturn(Optional.of(getCustomizedProduct()));

        deleteProduct.deleteProduct(productId);

        verify(database, times(1)).delete(argThat(productArgumentMatcher));
    }
}
