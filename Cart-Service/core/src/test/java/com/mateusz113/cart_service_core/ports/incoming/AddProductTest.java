package com.mateusz113.cart_service_core.ports.incoming;

import com.mateusz113.cart_service_core.CartServiceFacade;
import com.mateusz113.cart_service_core.CartServiceVerifier;
import com.mateusz113.cart_service_core.argument_matcher.CartArgumentMatcher;
import com.mateusz113.cart_service_core.ports.outgoing.CartServiceDatabase;
import com.mateusz113.cart_service_core.ports.outgoing.ProductServiceCommunicator;
import com.mateusz113.cart_service_model.cart.Cart;
import com.mateusz113.cart_service_model.exception.CartDoesNotExistException;
import com.mateusz113.cart_service_model.product.CustomizedProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

import static com.mateusz113.cart_service_core.util.CartServiceCoreTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AddProductTest {
    private AddProduct addProduct;
    private CartServiceDatabase database;
    private ProductServiceCommunicator communicator;

    @BeforeEach
    void setUp() {
        database = mock(CartServiceDatabase.class);
        communicator = mock(ProductServiceCommunicator.class);
        Clock clock = getClock();
        addProduct = new CartServiceFacade(database, communicator, new CartServiceVerifier(clock), clock);
    }

    @Test
    void add_CartWithIdDoesNotExist_ThrowsCartDoesNotExistException() {
        CustomizedProduct product = getCustomizedProduct();
        Long cartId = 2L;
        when(database.findCartById(cartId)).thenReturn(Optional.empty());

        CartDoesNotExistException exception = assertThrows(CartDoesNotExistException.class, () -> addProduct.add(product, cartId));

        assertEquals("Cart with given ID: %d, does not exist".formatted(cartId), exception.getMessage());
        assertEquals(404, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void add_CartExists_AddsProductToCart() {
        CustomizedProduct product = getCustomizedProduct();
        Long cartId = 1L;
        CustomizedProduct updatedProduct = getCustomizedProduct();
        updatedProduct.fillWithSourceData(getSourceProduct());
        Cart updatedCart = getCart();
        updatedCart.getCustomizedProducts().add(updatedProduct);
        ArgumentMatcher<Cart> cartArgumentMatcher = new CartArgumentMatcher(updatedCart);
        when(database.findCartById(cartId)).thenReturn(Optional.of(getCart()));
        when(communicator.getProductSourceData(product.getSourceId())).thenReturn(getSourceProduct());

        addProduct.add(product, cartId);

        verify(database, times(1)).save(argThat(cartArgumentMatcher));
    }
}
