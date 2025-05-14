package com.mateusz113.cart_service_core.ports.incoming;

import com.mateusz113.cart_service_core.CartServiceFacade;
import com.mateusz113.cart_service_core.CartServiceVerifier;
import com.mateusz113.cart_service_core.argument_matcher.CartArgumentMatcher;
import com.mateusz113.cart_service_core.ports.outgoing.CartServiceDatabase;
import com.mateusz113.cart_service_core.ports.outgoing.ProductServiceCommunicator;
import com.mateusz113.cart_service_model.cart.Cart;
import com.mateusz113.cart_service_model.exception.CartDoesNotExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import java.time.Clock;
import java.util.Optional;

import static com.mateusz113.cart_service_core.util.CartServiceCoreTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DeleteCartTest {
    private DeleteCart deleteCart;
    private CartServiceDatabase database;

    @BeforeEach
    void setUp() {
        database = mock(CartServiceDatabase.class);
        ProductServiceCommunicator communicator = mock(ProductServiceCommunicator.class);
        Clock clock = getClock();
        deleteCart = new CartServiceFacade(database, communicator, new CartServiceVerifier(clock), clock);
    }

    @Test
    void deleteCart_CartWithIdDoesNotExist_ThrowsCartDoesNotExistException() {
        Long cartId = 2L;
        when(database.findCartById(cartId)).thenReturn(Optional.empty());

        CartDoesNotExistException exception = assertThrows(CartDoesNotExistException.class, () -> deleteCart.deleteCart(cartId));

        assertEquals("Cart with given ID: %d, does not exist".formatted(cartId), exception.getMessage());
        assertEquals(404, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void deleteCart_CartExists_DeletesCart() {
        Long cartId = 2L;
        Cart cart = getCart();
        ArgumentMatcher<Cart> cartArgumentMatcher = new CartArgumentMatcher(cart);
        when(database.findCartById(cartId)).thenReturn(Optional.of(cart));

        deleteCart.deleteCart(cartId);

        verify(database, times(1)).delete(argThat(cartArgumentMatcher));
    }
}
