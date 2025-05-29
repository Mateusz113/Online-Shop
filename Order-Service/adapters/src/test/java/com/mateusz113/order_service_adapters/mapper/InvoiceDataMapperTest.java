package com.mateusz113.order_service_adapters.mapper;

import com.mateusz113.order_service_model.invoice.InvoiceData;
import com.mateusz113.order_service_model_public.dto.InvoiceDataDto;
import com.mateusz113.order_service_model_public.dto.InvoiceItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getDefaultPrice;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getDefaultQuantity;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getInvoiceData;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InvoiceDataMapperTest {
    private InvoiceDataMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(InvoiceDataMapper.class);
    }

    @Test
    void modelToDto_ReturnsCorrectDtoClass() {
        InvoiceData model = getInvoiceData();

        InvoiceDataDto result = mapper.modelToDto(model);

        assertEquals("from", result.from());
        assertEquals("to", result.to());
        assertEquals("shipTo", result.shipTo());
        assertEquals("number", result.number());
        assertEquals("currency", result.currency());
        assertEquals(getDefaultPrice().multiply(BigDecimal.TWO), result.amountPaid());
        assertEquals(2, result.items().size());
        for (InvoiceItemDto item : result.items()) {
            assertEquals("name", item.name());
            assertEquals(getDefaultQuantity(), item.quantity());
            assertEquals(getDefaultPrice(), item.price());
            assertEquals("description", item.description());
        }
    }
}
