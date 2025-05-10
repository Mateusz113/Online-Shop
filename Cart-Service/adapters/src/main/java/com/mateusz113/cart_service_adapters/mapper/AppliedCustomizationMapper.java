package com.mateusz113.cart_service_adapters.mapper;

import com.mateusz113.cart_service_adapters.entity.AppliedCustomizationEntity;
import com.mateusz113.cart_service_adapters.entity.AppliedCustomizationOptionEntity;
import com.mateusz113.cart_service_model.customization.AppliedCustomization;
import com.mateusz113.cart_service_model.customization.AppliedCustomizationOption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AppliedCustomizationMapper {
    @Mapping(target = "product", ignore = true)
    AppliedCustomizationEntity modelToEntity(AppliedCustomization appliedCustomization);

    default AppliedCustomizationOptionEntity appliedCustomizationOptionModelToEntity(AppliedCustomizationOption appliedCustomizationOption) {
        AppliedCustomizationOptionMapper mapper = Mappers.getMapper(AppliedCustomizationOptionMapper.class);
        return mapper.modelToEntity(appliedCustomizationOption);
    }
}
