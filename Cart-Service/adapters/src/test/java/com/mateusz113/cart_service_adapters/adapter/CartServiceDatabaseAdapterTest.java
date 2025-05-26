package com.mateusz113.cart_service_adapters.adapter;

import com.mateusz113.cart_service_adapters.argument_matcher.CartEntityArgumentMatcher;
import com.mateusz113.cart_service_adapters.argument_matcher.CustomizedProductEntityArgumentMatcher;
import com.mateusz113.cart_service_adapters.entity.CartEntity;
import com.mateusz113.cart_service_adapters.entity.CustomizedProductEntity;
import com.mateusz113.cart_service_adapters.mapper.CartMapper;
import com.mateusz113.cart_service_adapters.mapper.CustomizedProductMapper;
import com.mateusz113.cart_service_adapters.repository.CartRepository;
import com.mateusz113.cart_service_adapters.repository.CustomizedProductRepository;
import com.mateusz113.cart_service_model.cart.Cart;
import com.mateusz113.cart_service_model.customization.AppliedCustomization;
import com.mateusz113.cart_service_model.customization.AppliedCustomizationOption;
import com.mateusz113.cart_service_model.product.CustomizedProduct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatcher;

import java.util.Optional;

import static com.mateusz113.cart_service_adapters.util.CartServiceAdaptersTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class CartServiceDatabaseAdapterTest {
    private CartServiceDatabaseAdapter databaseAdapter;
    private CartRepository cartRepository;
    private CustomizedProductRepository productRepository;
    private CartMapper cartMapper;
    private CustomizedProductMapper productMapper;

    @BeforeEach
    void setUp() {
        cartRepository = mock(CartRepository.class);
        productRepository = mock(CustomizedProductRepository.class);
        cartMapper = Mappers.getMapper(CartMapper.class);
        productMapper = Mappers.getMapper(CustomizedProductMapper.class);
        databaseAdapter = new CartServiceDatabaseAdapter(cartRepository, productRepository, cartMapper, productMapper);
    }

    @Test
    void save_SavesCartAndReturnsIt() {
        Cart cart = getCart();
        CartEntity mappedCart = cartMapper.modelToEntity(cart);
        when(cartRepository.save(mappedCart)).thenReturn(mappedCart);

        Cart result = databaseAdapter.save(cart);

        assertEquals(1L, result.getId());
        assertEquals(getDefaultTime(), result.getCreationDate());
        assertEquals(2, result.getCustomizedProducts().size());
        for (CustomizedProduct product : result.getCustomizedProducts()) {
            assertEquals(1L, product.getId());
            assertEquals(1L, product.getSourceId());
            assertEquals("name", product.getName());
            assertEquals("brand", product.getBrand());
            assertEquals(getDefaultPrice(), product.getPrice());
            assertEquals(getDefaultQuantity(), product.getQuantity());
            assertNull(product.getCart());
            assertEquals(2, product.getAppliedCustomizations().size());
            for (AppliedCustomization customization : product.getAppliedCustomizations()) {
                assertEquals(1L, customization.getId());
                assertEquals(1L, customization.getSourceId());
                assertEquals("name", customization.getName());
                assertEquals(true, customization.getMultipleChoice());
                assertNull(customization.getProduct());
                assertEquals(2, customization.getAppliedOptions().size());
                for (AppliedCustomizationOption option : customization.getAppliedOptions()) {
                    assertEquals(1L, option.getId());
                    assertEquals(1L, option.getSourceId());
                    assertEquals("name", option.getName());
                    assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
                    assertNull(option.getCustomization());
                }
            }
        }
    }

    @Test
    void findCartById_ReturnsOptionalOfCart() {
        Long cartId = 1L;
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(getCartEntity()));

        Optional<Cart> result = databaseAdapter.findCartById(cartId);

        result.ifPresentOrElse(
                cart -> {
                    assertEquals(1L, cart.getId());
                    assertEquals(getDefaultTime(), cart.getCreationDate());
                    assertEquals(2, cart.getCustomizedProducts().size());
                    for (CustomizedProduct product : cart.getCustomizedProducts()) {
                        assertEquals(1L, product.getId());
                        assertEquals(1L, product.getSourceId());
                        assertEquals("name", product.getName());
                        assertEquals("brand", product.getBrand());
                        assertEquals(getDefaultPrice(), product.getPrice());
                        assertEquals(getDefaultQuantity(), product.getQuantity());
                        assertNull(product.getCart());
                        assertEquals(2, product.getAppliedCustomizations().size());
                        for (AppliedCustomization customization : product.getAppliedCustomizations()) {
                            assertEquals(1L, customization.getId());
                            assertEquals(1L, customization.getSourceId());
                            assertEquals("name", customization.getName());
                            assertEquals(true, customization.getMultipleChoice());
                            assertNull(customization.getProduct());
                            assertEquals(2, customization.getAppliedOptions().size());
                            for (AppliedCustomizationOption option : customization.getAppliedOptions()) {
                                assertEquals(1L, option.getId());
                                assertEquals(1L, option.getSourceId());
                                assertEquals("name", option.getName());
                                assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
                                assertNull(option.getCustomization());
                            }
                        }
                    }
                },
                Assertions::fail
        );
    }

    @Test
    void findProductById_ReturnsOptionalOfCustomizedProduct() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(getCustomizedProductEntity()));

        Optional<CustomizedProduct> result = databaseAdapter.findProductById(productId);

        result.ifPresentOrElse(
                product -> {
                    assertEquals(1L, product.getId());
                    assertEquals(1L, product.getSourceId());
                    assertEquals("name", product.getName());
                    assertEquals("brand", product.getBrand());
                    assertEquals(getDefaultPrice(), product.getPrice());
                    assertEquals(getDefaultQuantity(), product.getQuantity());
                    assertNull(product.getCart());
                    assertEquals(2, product.getAppliedCustomizations().size());
                    for (AppliedCustomization customization : product.getAppliedCustomizations()) {
                        assertEquals(1L, customization.getId());
                        assertEquals(1L, customization.getSourceId());
                        assertEquals("name", customization.getName());
                        assertEquals(true, customization.getMultipleChoice());
                        assertNull(customization.getProduct());
                        assertEquals(2, customization.getAppliedOptions().size());
                        for (AppliedCustomizationOption option : customization.getAppliedOptions()) {
                            assertEquals(1L, option.getId());
                            assertEquals(1L, option.getSourceId());
                            assertEquals("name", option.getName());
                            assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
                            assertNull(option.getCustomization());
                        }
                    }
                },
                Assertions::fail
        );
    }

    @Test
    void delete_DeletesCart() {
        Cart cart = getCart();
        ArgumentMatcher<CartEntity> cartEntityArgumentMatcher = new CartEntityArgumentMatcher(cartMapper.modelToEntity(cart));

        databaseAdapter.delete(cart);

        verify(cartRepository, times(1)).delete(argThat(cartEntityArgumentMatcher));
    }

    @Test
    void delete_DeletesProduct() {
        CustomizedProduct product = getCustomizedProduct();
        ArgumentMatcher<CustomizedProductEntity> productEntityArgumentMatcher = new CustomizedProductEntityArgumentMatcher(productMapper.modelToEntity(product));

        databaseAdapter.delete(product);

        verify(productRepository, times(1)).delete(argThat(productEntityArgumentMatcher));
    }
}
