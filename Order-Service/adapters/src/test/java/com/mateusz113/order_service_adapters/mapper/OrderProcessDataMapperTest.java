package com.mateusz113.order_service_adapters.mapper;

import com.mateusz113.order_service_model.event.EventType;
import com.mateusz113.order_service_model.order.OrderProcessingData;
import com.mateusz113.order_service_model_public.command.ProcessOrderCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getOrderProcessingData;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getProcessOrderCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class OrderProcessDataMapperTest {
    private OrderProcessDataMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(OrderProcessDataMapper.class);
    }

    @Test
    void commandToModel_ReturnsCorrectModelInstance() {
        ProcessOrderCommand command = getProcessOrderCommand();

        OrderProcessingData result = mapper.commandToModel(command);

        assertEquals(1L, result.orderId());
        assertNull(result.eventType());
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

    @Test
    void modelToCommand_ReturnsCorrectCommandInstance() {
        OrderProcessingData model = getOrderProcessingData(EventType.GENERATE_INVOICE);

        ProcessOrderCommand result = mapper.modelToCommand(model);

        assertEquals(1L, result.orderId());
        assertEquals("GENERATE_INVOICE", result.eventType());
        assertEquals("firstName", result.clientDto().firstName());
        assertEquals("lastName", result.clientDto().lastName());
        assertEquals("email", result.clientDto().email());
        assertEquals("phoneNumber", result.clientDto().phoneNumber());
        assertEquals("country", result.clientDto().address().country());
        assertEquals("city", result.clientDto().address().city());
        assertEquals("zipCode", result.clientDto().address().zipCode());
        assertEquals("street", result.clientDto().address().street());
        assertEquals("buildingNumber", result.clientDto().address().buildingNumber());
        assertEquals("apartmentNumber", result.clientDto().address().apartmentNumber());
    }
}
