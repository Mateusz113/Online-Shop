package com.mateusz113.order_service_adapters.mapper;

import com.mateusz113.order_service_model.client.Client;
import com.mateusz113.order_service_model_public.dto.ClientDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getClientDto;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientMapperTest {
    private ClientMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ClientMapper.class);
    }

    @Test
    void dtoToModel_ReturnsCorrectModelInstance() {
        ClientDto dto = getClientDto();

        Client result = mapper.dtoToModel(dto);

        assertEquals("firstName", result.firstName());
        assertEquals("lastName", result.lastName());
        assertEquals("email", result.email());
        assertEquals("phoneNumber", result.phoneNumber());
        assertEquals("country", result.address().country());
        assertEquals("city", result.address().city());
        assertEquals("zipCode", result.address().zipCode());
        assertEquals("street", result.address().street());
        assertEquals("buildingNumber", result.address().buildingNumber());
        assertEquals("apartmentNumber", result.address().apartmentNumber());
    }
}
