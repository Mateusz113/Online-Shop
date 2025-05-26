package com.mateusz113.cart_service_core.ports.incoming;

import com.mateusz113.cart_service_core.CartServiceFacade;
import com.mateusz113.cart_service_core.CartServiceVerifier;
import com.mateusz113.cart_service_core.ports.outgoing.CartServiceDatabase;
import com.mateusz113.cart_service_core.ports.outgoing.ProductServiceCommunicator;
import com.mateusz113.cart_service_model.cart.Cart;
import com.mateusz113.cart_service_model.customization.AppliedCustomization;
import com.mateusz113.cart_service_model.customization.AppliedCustomizationOption;
import com.mateusz113.cart_service_model.product.CustomizedProduct;
import com.mateusz113.cart_service_model.product.SourceProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static com.mateusz113.cart_service_core.util.CartServiceCoreTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AddCartTest {
    private AddCart addCart;
    private CartServiceDatabase database;
    private ProductServiceCommunicator communicator;

    @BeforeEach
    void setUp() {
        database = mock(CartServiceDatabase.class);
        communicator = mock(ProductServiceCommunicator.class);
        Clock clock = getClock();
        addCart = new CartServiceFacade(database, communicator, new CartServiceVerifier(clock), clock);
    }

    @Test
    void add_DataIsCorrect_ReturnsSavedCart() {
        Cart cart = getCart();
        Cart updatedCart = getCart();
        SourceProduct sourceProduct = getSourceProduct();
        updatedCart.getCustomizedProducts().forEach(customizedProduct -> customizedProduct.fillWithSourceData(sourceProduct));
        when(communicator.getProductSourceData(1L)).thenReturn(sourceProduct);
        when(database.save(updatedCart)).thenReturn(updatedCart);

        Cart result = addCart.add(cart);

        assertEquals(1L, result.getId());
        assertEquals(getDefaultTime(), result.getCreationDate());
        for (CustomizedProduct customizedProduct : result.getCustomizedProducts()) {
            assertEquals(1L, customizedProduct.getId());
            assertEquals(1L, customizedProduct.getSourceId());
            assertEquals("source_name", customizedProduct.getName());
            assertEquals("source_brand", customizedProduct.getBrand());
            assertEquals(getDefaultPrice(), customizedProduct.getPrice());
            assertEquals(getDefaultQuantity(), customizedProduct.getQuantity());
            assertNull(customizedProduct.getCart());
            for (AppliedCustomization appliedCustomization : customizedProduct.getAppliedCustomizations()) {
                assertEquals(1L, appliedCustomization.getId());
                assertEquals(1L, appliedCustomization.getSourceId());
                assertEquals("source_name", appliedCustomization.getName());
                assertEquals(true, appliedCustomization.getMultipleChoice());
                assertNull(appliedCustomization.getProduct());
                for (AppliedCustomizationOption appliedOption : appliedCustomization.getAppliedOptions()) {
                    assertEquals(1L, appliedOption.getId());
                    assertEquals(1L, appliedOption.getSourceId());
                    assertEquals("source_name", appliedOption.getName());
                    assertEquals(getDefaultPriceDifference(), appliedOption.getPriceDifference());
                    assertNull(appliedOption.getCustomization());
                }
            }
        }
    }
}
