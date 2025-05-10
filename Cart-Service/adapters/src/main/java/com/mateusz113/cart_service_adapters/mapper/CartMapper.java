package com.mateusz113.cart_service_adapters.mapper;

import com.mateusz113.cart_service_adapters.entity.CartEntity;
import com.mateusz113.cart_service_adapters.entity.CustomizedProductEntity;
import com.mateusz113.cart_service_model.cart.Cart;
import com.mateusz113.cart_service_model.product.CustomizedProduct;
import com.mateusz113.cart_service_model_public.command.InsertCartCommand;
import com.mateusz113.cart_service_model_public.dto.CartDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartEntity modelToEntity(Cart cart);

    Cart entityToModel(CartEntity cartEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    Cart commandToModel(InsertCartCommand insertCartCommand);

    CartDto modelToDto(Cart cart);

    default CustomizedProductEntity customizedProductModelToEntity(CustomizedProduct customizedProduct) {
        CustomizedProductMapper mapper = Mappers.getMapper(CustomizedProductMapper.class);
        return mapper.modelToEntity(customizedProduct);
    }
}
