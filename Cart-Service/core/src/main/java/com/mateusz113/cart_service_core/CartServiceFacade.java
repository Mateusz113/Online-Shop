package com.mateusz113.cart_service_core;

import com.mateusz113.cart_service_core.ports.incoming.AddCart;
import com.mateusz113.cart_service_core.ports.incoming.AddProduct;
import com.mateusz113.cart_service_core.ports.incoming.DeleteCart;
import com.mateusz113.cart_service_core.ports.incoming.DeleteProduct;
import com.mateusz113.cart_service_core.ports.incoming.GetCart;
import com.mateusz113.cart_service_core.ports.outgoing.CartServiceDatabase;
import com.mateusz113.cart_service_core.ports.outgoing.ProductServiceCommunicator;
import com.mateusz113.cart_service_model.cart.Cart;
import com.mateusz113.cart_service_model.exception.CartDoesNotExistException;
import com.mateusz113.cart_service_model.exception.CustomizedProductDoesNotExistException;
import com.mateusz113.cart_service_model.product.CustomizedProduct;
import com.mateusz113.cart_service_model.product.SourceProduct;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.OffsetDateTime;

@RequiredArgsConstructor
public class CartServiceFacade implements AddCart, AddProduct, DeleteCart, DeleteProduct, GetCart {
    private final CartServiceDatabase database;
    private final ProductServiceCommunicator productServiceCommunicator;
    private final CartServiceVerifier verifier;
    private final Clock clock;

    @Override
    public Cart add(Cart cart) {
        cart.setCreationDate(OffsetDateTime.now(clock));
        cart.getCustomizedProducts().forEach(customizedProduct -> {
            verifier.verifyCustomizedProduct(customizedProduct);
            updateCustomizedProductWithSourceData(customizedProduct);
        });
        return database.save(cart);
    }

    @Override
    public void add(CustomizedProduct customizedProduct, Long cartId) {
        Cart cart = findCartById(cartId);
        verifier.verifyCustomizedProduct(customizedProduct);
        updateCustomizedProductWithSourceData(customizedProduct);
        cart.getCustomizedProducts().add(customizedProduct);
        database.save(cart);
    }

    @Override
    public void deleteCart(Long cartId) {
        Cart cart = findCartById(cartId);
        database.delete(cart);
    }

    @Override
    public void deleteProduct(Long customizedProductId) {
        CustomizedProduct customizedProduct = findCustomizedProductById(customizedProductId);
        database.delete(customizedProduct);
    }

    @Override
    public Cart getCart(Long cartId) {
        return findCartById(cartId);
    }

    private void updateCustomizedProductWithSourceData(CustomizedProduct customizedProduct) {
        SourceProduct sourceProduct = productServiceCommunicator.getProductSourceData(customizedProduct.getSourceId());
        verifier.validateCustomizedProductAgainstSourceData(sourceProduct, customizedProduct);
        customizedProduct.fillWithSourceData(sourceProduct);
    }

    private Cart findCartById(Long cartId) {
        return database.findCartById(cartId)
                .orElseThrow(() -> new CartDoesNotExistException("Cart with given ID: %d, does not exist".formatted(cartId), OffsetDateTime.now(clock)));
    }

    private CustomizedProduct findCustomizedProductById(Long productId) {
        return database.findProductById(productId)
                .orElseThrow(() -> new CustomizedProductDoesNotExistException("Product with given ID: %d, does not exist".formatted(productId), OffsetDateTime.now(clock)));
    }
}
