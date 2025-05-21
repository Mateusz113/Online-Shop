package com.mateusz113.order_service_adapters.mapper;

import com.mateusz113.order_service_model.product.Customization;
import com.mateusz113.order_service_model_public.dto.CartCustomizationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomizationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "options", source = "appliedOptions")
    Customization dtoToModel(CartCustomizationDto dto);
}
