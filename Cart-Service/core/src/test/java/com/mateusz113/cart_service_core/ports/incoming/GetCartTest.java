package com.mateusz113.cart_service_core.ports.incoming;

import com.mateusz113.cart_service_core.CartServiceFacade;
import com.mateusz113.cart_service_core.CartServiceVerifier;
import com.mateusz113.cart_service_core.ports.outgoing.CartServiceDatabase;
import com.mateusz113.cart_service_core.ports.outgoing.ProductServiceCommunicator;
import com.mateusz113.cart_service_model.cart.Cart;
import com.mateusz113.cart_service_model.customization.AppliedCustomization;
import com.mateusz113.cart_service_model.customization.AppliedCustomizationOption;
import com.mateusz113.cart_service_model.exception.CartDoesNotExistException;
import com.mateusz113.cart_service_model.product.CustomizedProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.Optional;

import static com.mateusz113.cart_service_core.util.CartServiceCoreTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetCartTest {
    private GetCart getCart;
    private CartServiceDatabase database;

    @BeforeEach
    void setUp() {
        database = mock(CartServiceDatabase.class);
        ProductServiceCommunicator communicator = mock(ProductServiceCommunicator.class);
        Clock clock = getClock();
        getCart = new CartServiceFacade(database, communicator, new CartServiceVerifier(clock), clock);
    }

    @Test
    void getCartCart_CartWithIdDoesNotExist_ThrowsCartDoesNotExistException() {
        Long cartId = 2L;
        when(database.findCartById(cartId)).thenReturn(Optional.empty());

        CartDoesNotExistException exception = assertThrows(CartDoesNotExistException.class, () -> getCart.getCart(cartId));

        assertEquals("Cart with given ID: %d, does not exist".formatted(cartId), exception.getMessage());
        assertEquals(404, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void getCartCart_CartExists_ReturnsCart() {
        Long cartId = 2L;
        when(database.findCartById(cartId)).thenReturn(Optional.of(getCart()));

        Cart result = getCart.getCart(cartId);

        assertEquals(1L, result.getId());
        assertEquals(getDefaultTime(), result.getCreationDate());
        for (CustomizedProduct customizedProduct : result.getCustomizedProducts()) {
            assertEquals(1L, customizedProduct.getId());
            assertEquals(1L, customizedProduct.getSourceId());
            assertEquals("name", customizedProduct.getName());
            assertEquals("brand", customizedProduct.getBrand());
            assertEquals(getDefaultPrice(), customizedProduct.getPrice());
            assertEquals(getDefaultQuantity(), customizedProduct.getQuantity());
            assertNull(customizedProduct.getCart());
            for (AppliedCustomization appliedCustomization : customizedProduct.getAppliedCustomizations()) {
                assertEquals(1L, appliedCustomization.getId());
                assertEquals(1L, appliedCustomization.getSourceId());
                assertEquals("name", appliedCustomization.getName());
                assertEquals(true, appliedCustomization.getMultipleChoice());
                assertNull(appliedCustomization.getProduct());
                for (AppliedCustomizationOption appliedOption : appliedCustomization.getAppliedOptions()) {
                    assertEquals(1L, appliedOption.getId());
                    assertEquals(1L, appliedOption.getSourceId());
                    assertEquals("name", appliedOption.getName());
                    assertEquals(getDefaultPriceDifference(), appliedOption.getPriceDifference());
                    assertNull(appliedOption.getCustomization());
                }
            }
        }
    }
}
