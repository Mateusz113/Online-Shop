package com.mateusz113.cart_service_adapters.argument_matcher;

import com.mateusz113.cart_service_adapters.entity.CustomizedProductEntity;
import lombok.RequiredArgsConstructor;
import org.mockito.ArgumentMatcher;

import java.util.Objects;

@RequiredArgsConstructor
public class CustomizedProductEntityArgumentMatcher implements ArgumentMatcher<CustomizedProductEntity> {
    private final CustomizedProductEntity left;

    @Override
    public boolean matches(CustomizedProductEntity right) {
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

