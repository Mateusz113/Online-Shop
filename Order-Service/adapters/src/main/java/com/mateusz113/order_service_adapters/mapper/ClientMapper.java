package com.mateusz113.order_service_adapters.mapper;

import com.mateusz113.order_service_model.client.Client;
import com.mateusz113.order_service_model_public.dto.ClientDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    Client dtoToModel(ClientDto dto);
}
