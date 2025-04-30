package com.mateusz113.product_service_adapters.mapper;

import com.mateusz113.product_service_adapters.entity.CustomizationOptionEntity;
import com.mateusz113.product_service_model.customization.CustomizationOption;
import com.mateusz113.product_service_model_public.commands.UpsertCustomizationOptionCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomizationOptionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customizationElement", ignore = true)
    CustomizationOption commandToModel(UpsertCustomizationOptionCommand upsertCustomizationOptionCommand);

    @Mapping(target = "customizationElement", ignore = true)
    CustomizationOptionEntity modelToEntity(CustomizationOption customizationOption);
}


