package com.mateusz113.cart_service_adapters.mapper;

import com.mateusz113.cart_service_adapters.entity.AppliedCustomizationOptionEntity;
import com.mateusz113.cart_service_model.customization.AppliedCustomizationOption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppliedCustomizationOptionMapper {
    @Mapping(target = "customization", ignore = true)
    AppliedCustomizationOptionEntity modelToEntity(AppliedCustomizationOption appliedCustomizationOption);
}
