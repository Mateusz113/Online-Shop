package com.mateusz113.order_service_adapters.mapper;

import com.mateusz113.order_service_model.content.PageableContent;
import com.mateusz113.order_service_model.order.Order;
import com.mateusz113.order_service_model_public.dto.OrderDto;
import com.mateusz113.order_service_model_public.dto.PageableContentDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PageableContentMapper {
    PageableContentDto<OrderDto> modelToDto(PageableContent<Order> model);

    default OrderDto orderModelToDto(Order model) {
        OrderMapper mapper = Mappers.getMapper(OrderMapper.class);
        return mapper.modelToDto(model);
    }
}
