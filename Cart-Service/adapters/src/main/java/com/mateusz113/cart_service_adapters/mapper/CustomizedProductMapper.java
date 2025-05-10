package com.mateusz113.cart_service_adapters.mapper;

import com.mateusz113.cart_service_adapters.entity.AppliedCustomizationEntity;
import com.mateusz113.cart_service_adapters.entity.CustomizedProductEntity;
import com.mateusz113.cart_service_model.customization.AppliedCustomization;
import com.mateusz113.cart_service_model.product.CustomizedProduct;
import com.mateusz113.cart_service_model_public.command.InsertCustomizedProductCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CustomizedProductMapper {
    @Mapping(target = "cart", ignore = true)
    CustomizedProductEntity modelToEntity(CustomizedProduct customizedProduct);

    CustomizedProduct entityToModel(CustomizedProductEntity customizedProductEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "brand", ignore = true)
    CustomizedProduct commandToModel(InsertCustomizedProductCommand command);

    default AppliedCustomizationEntity appliedCustomizationModelToEntity(AppliedCustomization appliedCustomization) {
        AppliedCustomizationMapper mapper = Mappers.getMapper(AppliedCustomizationMapper.class);
        return mapper.modelToEntity(appliedCustomization);
    }
}
