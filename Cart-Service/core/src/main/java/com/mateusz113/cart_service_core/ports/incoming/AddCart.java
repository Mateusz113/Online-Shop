package com.mateusz113.cart_service_core.ports.incoming;

import com.mateusz113.cart_service_model.cart.Cart;

public interface AddCart {
    Cart add(Cart cart);
}
