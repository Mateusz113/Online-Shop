package com.mateusz113.product_service_adapters.mapper;

import com.mateusz113.product_service_adapters.entity.ProductDetailEntity;
import com.mateusz113.product_service_model.product.ProductDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductDetailMapper {
    @Mapping(target = "product", ignore = true)
    ProductDetailEntity modelToEntity(ProductDetail productDetail);
}
