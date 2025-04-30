package com.mateusz113.product_service_adapters.mapper;

import com.mateusz113.product_service_adapters.entity.CustomizationElementEntity;
import com.mateusz113.product_service_model.customization.CustomizationElement;
import com.mateusz113.product_service_model_public.commands.UpsertCustomizationElementCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CustomizationOptionMapper.class)
public interface CustomizationElementMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "options", source = "customizationOptions")
    CustomizationElement commandToModel(UpsertCustomizationElementCommand upsertCustomizationElementCommand);

    @Mapping(target = "product", ignore = true)
    CustomizationElementEntity modelToEntity(CustomizationElement customizationElement);
}
