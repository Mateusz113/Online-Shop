package com.mateusz113.order_service_adapters.mapper;

import com.mateusz113.order_service_model.order.OrderStatusUpdateData;
import com.mateusz113.order_service_model_public.command.UpdateOrderStatusCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderStatusUpdateMapper {
    @Mapping(target = "client", source = "clientDto")
    OrderStatusUpdateData commandToModel(UpdateOrderStatusCommand command);
}
