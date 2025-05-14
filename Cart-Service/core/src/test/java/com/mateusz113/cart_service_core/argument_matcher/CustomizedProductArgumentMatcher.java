package com.mateusz113.cart_service_core.argument_matcher;

import com.mateusz113.cart_service_model.product.CustomizedProduct;
import lombok.RequiredArgsConstructor;
import org.mockito.ArgumentMatcher;

import java.util.Objects;

@RequiredArgsConstructor
public class CustomizedProductArgumentMatcher implements ArgumentMatcher<CustomizedProduct> {
    private final CustomizedProduct left;

    @Override
    public boolean matches(CustomizedProduct right) {
        return Objects.equals(left.getId(), right.getId())
                && Objects.equals(left.getSourceId(), right.getSourceId())
                && Objects.equals(left.getName(), right.getName())
                && Objects.equals(left.getBrand(), right.getBrand())
                && Objects.equals(left.getPrice(), right.getPrice())
                && Objects.equals(left.getQuantity(), right.getQuantity())
                && Objects.equals(left.getCart(), right.getCart())
                && Objects.equals(left.getAppliedCustomizations(), right.getAppliedCustomizations());
    }
}
