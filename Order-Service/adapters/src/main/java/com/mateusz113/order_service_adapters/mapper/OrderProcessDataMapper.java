package com.mateusz113.order_service_adapters.mapper;

import com.mateusz113.order_service_model.order.OrderProcessingData;
import com.mateusz113.order_service_model_public.command.ProcessOrderCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderProcessDataMapper {
    @Mapping(target = "client", source = "clientDto")
    OrderProcessingData commandToModel(ProcessOrderCommand command);

    @Mapping(target = "clientDto", source = "client")
    ProcessOrderCommand modelToCommand(OrderProcessingData model);
}
