package com.mateusz113.product_service_adapters.mapper;

import com.mateusz113.product_service_adapters.entity.CustomizationElementEntity;
import com.mateusz113.product_service_adapters.entity.CustomizationOptionEntity;
import com.mateusz113.product_service_model.customization.CustomizationElement;
import com.mateusz113.product_service_model.customization.CustomizationOption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CustomizationElementMapper {
    @Mapping(target = "product", ignore = true)
    CustomizationElementEntity modelToEntity(CustomizationElement customizationElement);

    default CustomizationOptionEntity customizationElementModelToEntity(CustomizationOption customizationOption) {
        CustomizationOptionMapper mapper = Mappers.getMapper(CustomizationOptionMapper.class);
        return mapper.modelToEntity(customizationOption);
    }
}
