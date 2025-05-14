package com.mateusz113.cart_service_adapters.argument_matcher;

import com.mateusz113.cart_service_adapters.entity.CartEntity;
import lombok.RequiredArgsConstructor;
import org.mockito.ArgumentMatcher;

import java.util.Objects;

@RequiredArgsConstructor
public class CartEntityArgumentMatcher implements ArgumentMatcher<CartEntity> {
    private final CartEntity left;

    @Override
    public boolean matches(CartEntity right) {
        return Objects.equals(left.getId(), right.getId())
                && Objects.equals(left.getCreationDate(), right.getCreationDate())
                && Objects.equals(left.getCustomizedProducts(), right.getCustomizedProducts());
    }
}
