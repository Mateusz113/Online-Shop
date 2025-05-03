package com.mateusz113.product_service_adapters.specification;

import com.mateusz113.product_service_adapters.entity.ProductEntity;
import com.mateusz113.product_service_model.filter.ProductFilter;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Objects;

public class ProductSpecification {
    public static Specification<ProductEntity> getSpecificationFromFilter(ProductFilter filter) {
        Specification<ProductEntity> specification = Specification.where(null);
        if (Objects.nonNull(filter.name())) {
            specification = specification.and(nameContains(filter.name()));
        }
        if (Objects.nonNull(filter.brand())) {
            specification = specification.and(brandContains(filter.brand()));
        }
        if (Objects.nonNull(filter.minPrice())) {
            specification = specification.and(priceIsAbove(filter.minPrice()));
        }
        if (Objects.nonNull(filter.maxPrice())) {
            specification = specification.and(priceIsBelow(filter.maxPrice()));
        }
        if (Objects.nonNull(filter.type())) {
            specification = specification.and(typeEquals(filter.type()));
        }
        if (Objects.nonNull(filter.minAvailableAmount())) {
            specification = specification.and(availableAmountIsAbove(filter.minAvailableAmount()));
        }
        return specification;
    }

    private static Specification<ProductEntity> nameContains(String name) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("name"), "%" + name + "%"));
    }

    private static Specification<ProductEntity> brandContains(String brand) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("brand"), "%" + brand + "%"));
    }

    private static Specification<ProductEntity> priceIsAbove(BigDecimal minPrice) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.ge(root.get("price"), minPrice));
    }

    private static Specification<ProductEntity> priceIsBelow(BigDecimal maxPrice) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.le(root.get("price"), maxPrice));
    }

    private static Specification<ProductEntity> typeEquals(String type) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("type"), type));
    }

    private static Specification<ProductEntity> availableAmountIsAbove(Integer minAvailableAmount) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.ge(root.get("availableAmount"), minAvailableAmount));
    }
}
