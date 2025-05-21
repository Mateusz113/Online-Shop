package com.mateusz113.order_service_adapters.mapper;

import com.mateusz113.order_service_adapters.entity.OrderEntity;
import com.mateusz113.order_service_model.order.Order;
import com.mateusz113.order_service_model_public.command.InsertOrderCommand;
import com.mateusz113.order_service_model_public.dto.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderEntity modelToEntity(Order model);

    Order entityToModel(OrderEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "placementTime", ignore = true)
    @Mapping(target = "orderStatus", ignore = true)
    @Mapping(target = "invoice", ignore = true)
    Order commandToModel(InsertOrderCommand command);

    OrderDto modelToDto(Order order);
}
