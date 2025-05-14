package com.mateusz113.cart_service_core.argument_matcher;

import com.mateusz113.cart_service_model.cart.Cart;
import lombok.RequiredArgsConstructor;
import org.mockito.ArgumentMatcher;

import java.util.Objects;

@RequiredArgsConstructor
public class CartArgumentMatcher implements ArgumentMatcher<Cart> {
    private final Cart left;

    @Override
    public boolean matches(Cart right) {
        return Objects.equals(left.getId(), right.getId())
                && Objects.equals(left.getCreationDate(), right.getCreationDate())
                && Objects.equals(left.getCustomizedProducts(), right.getCustomizedProducts());
    }
}
