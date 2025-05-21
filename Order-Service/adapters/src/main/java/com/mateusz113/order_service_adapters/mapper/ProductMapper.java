package com.mateusz113.order_service_adapters.mapper;

import com.mateusz113.order_service_model.product.Customization;
import com.mateusz113.order_service_model.product.Product;
import com.mateusz113.order_service_model_public.dto.CartCustomizationDto;
import com.mateusz113.order_service_model_public.dto.CartProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customizations", source = "appliedCustomizations")
    Product dtoToModel(CartProductDto cartProductDto);

    default Customization customizationDtoToModel(CartCustomizationDto dto) {
        CustomizationMapper mapper = Mappers.getMapper(CustomizationMapper.class);
        return mapper.dtoToModel(dto);
    }
}
