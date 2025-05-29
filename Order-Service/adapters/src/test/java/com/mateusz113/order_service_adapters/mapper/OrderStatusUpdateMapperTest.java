package com.mateusz113.order_service_adapters.mapper;

import com.mateusz113.order_service_model.order.OrderStatusUpdateData;
import com.mateusz113.order_service_model_public.command.UpdateOrderStatusCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getUpdateOrderStatusCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderStatusUpdateMapperTest {
    private OrderStatusUpdateMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(OrderStatusUpdateMapper.class);
    }

    @Test
    void commandToModel_ReturnsCorrectModelInstance() {
        UpdateOrderStatusCommand command = getUpdateOrderStatusCommand();

        OrderStatusUpdateData result = mapper.commandToModel(command);

        assertEquals(1L, result.orderId());
        assertEquals("firstName", result.client().firstName());
        assertEquals("lastName", result.client().lastName());
        assertEquals("email", result.client().email());
        assertEquals("phoneNumber", result.client().phoneNumber());
        assertEquals("country", result.client().address().country());
        assertEquals("city", result.client().address().city());
        assertEquals("zipCode", result.client().address().zipCode());
        assertEquals("street", result.client().address().street());
        assertEquals("buildingNumber", result.client().address().buildingNumber());
        assertEquals("apartmentNumber", result.client().address().apartmentNumber());
    }
}
