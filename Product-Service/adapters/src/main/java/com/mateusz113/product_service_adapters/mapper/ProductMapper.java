package com.mateusz113.product_service_adapters.mapper;

import com.mateusz113.product_service_adapters.entity.CustomizationElementEntity;
import com.mateusz113.product_service_adapters.entity.ProductDetailEntity;
import com.mateusz113.product_service_adapters.entity.ProductEntity;
import com.mateusz113.product_service_model.customization.CustomizationElement;
import com.mateusz113.product_service_model.product.Product;
import com.mateusz113.product_service_model.product.ProductDetail;
import com.mateusz113.product_service_model_public.commands.UpsertProductCommand;
import com.mateusz113.product_service_model_public.dto.DetailedProductDto;
import com.mateusz113.product_service_model_public.dto.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductEntity modelToEntity(Product product);

    Product entityToModel(ProductEntity entity);

    ProductDto modelToDto(Product product);

    DetailedProductDto modelToDetailedDto(Product product);

    List<ProductDto> modelListToDtoList(List<Product> products);

    @Mapping(target = "id", ignore = true)
    Product commandToModel(UpsertProductCommand upsertProductCommand);

    List<Product> commandListToModelList(List<UpsertProductCommand> upsertProductCommands);

    default ProductDetailEntity productDetailModelToEntity(ProductDetail productDetail) {
        ProductDetailMapper mapper = Mappers.getMapper(ProductDetailMapper.class);
        return mapper.modelToEntity(productDetail);
    }

    default CustomizationElementEntity customizationElementModelToEntity(CustomizationElement customizationElement) {
        CustomizationElementMapper mapper = Mappers.getMapper(CustomizationElementMapper.class);
        return mapper.modelToEntity(customizationElement);
    }
}
