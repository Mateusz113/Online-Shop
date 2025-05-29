package com.mateusz113.product_service_adapters.arument_matcher;

import com.mateusz113.product_service_adapters.entity.ProductEntity;
import lombok.RequiredArgsConstructor;
import org.mockito.ArgumentMatcher;

import java.util.Objects;

@RequiredArgsConstructor
public class ProductEntityArgumentMatcher implements ArgumentMatcher<ProductEntity> {
    private final ProductEntity left;

    @Override
    public boolean matches(ProductEntity right) {
        return Objects.equals(left.getId(), right.getId())
                && Objects.equals(left.getName(), right.getName())
                && Objects.equals(left.getBrand(), right.getBrand())
                && Objects.equals(left.getPrice(), right.getPrice())
                && Objects.equals(left.getType(), right.getType())
                && Objects.equals(left.getAvailableAmount(), right.getAvailableAmount())
                && Objects.equals(left.getCustomizations(), right.getCustomizations());
    }
}
