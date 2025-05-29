package com.mateusz113.product_service_adapters.mapper;

import com.mateusz113.product_service_adapters.entity.CustomizationOptionEntity;
import com.mateusz113.product_service_model.customization.CustomizationOption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomizationOptionMapper {
    @Mapping(target = "customizationElement", ignore = true)
    CustomizationOptionEntity modelToEntity(CustomizationOption customizationOption);
}


