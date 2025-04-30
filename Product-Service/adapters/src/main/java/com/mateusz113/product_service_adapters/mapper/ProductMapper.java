package com.mateusz113.product_service_adapters.mapper;

import com.mateusz113.product_service_adapters.entity.ProductEntity;
import com.mateusz113.product_service_model.product.Product;
import com.mateusz113.product_service_model_public.commands.UpsertProductCommand;
import com.mateusz113.product_service_model_public.dto.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = CustomizationElementMapper.class)
public interface ProductMapper {
    ProductEntity modelToEntity(Product product);

    Product entityToModel(ProductEntity entity);

    ProductDto modelToDto(Product product);

    List<ProductDto> modelListToDtoList(List<Product> products);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customizations", source = "customizationElements")
    Product commandToModel(UpsertProductCommand upsertProductCommand);

    List<Product> commandListToModelList(List<UpsertProductCommand> upsertProductCommands);
}
