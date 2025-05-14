package com.mateusz113.cart_service_core.ports.outgoing;

import com.mateusz113.cart_service_model.cart.Cart;
import com.mateusz113.cart_service_model.product.CustomizedProduct;

import java.util.Optional;

public interface CartServiceDatabase {
    Cart save(Cart cart);

    Optional<Cart> findCartById(Long id);

    Optional<CustomizedProduct> findProductById(Long id);

    void delete(Cart cart);

    void delete(CustomizedProduct customizedProduct);
}
