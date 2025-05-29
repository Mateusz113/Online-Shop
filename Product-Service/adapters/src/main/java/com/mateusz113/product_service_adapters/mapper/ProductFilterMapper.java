package com.mateusz113.product_service_adapters.mapper;

import com.mateusz113.product_service_model.filter.ProductFilter;
import com.mateusz113.product_service_model_public.commands.FilterProductCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductFilterMapper {
    ProductFilter commandToModel(FilterProductCommand command);
}
