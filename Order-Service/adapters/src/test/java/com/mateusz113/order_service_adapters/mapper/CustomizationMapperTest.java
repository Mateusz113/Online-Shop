package com.mateusz113.order_service_adapters.mapper;

import com.mateusz113.order_service_model.product.Customization;
import com.mateusz113.order_service_model.product.CustomizationOption;
import com.mateusz113.order_service_model_public.dto.CartCustomizationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getCartCustomizationDto;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getDefaultPriceDifference;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CustomizationMapperTest {
    private CustomizationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(CustomizationMapper.class);
    }

    @Test
    void dtoToModel_ReturnsCorrectModelInstance() {
        CartCustomizationDto dto = getCartCustomizationDto();

        Customization result = mapper.dtoToModel(dto);

        assertNull(result.getId());
        assertEquals("name", result.getName());
        assertEquals(true, result.getMultipleChoice());
        for (CustomizationOption option : result.getOptions()) {
            assertNull(option.getId());
            assertEquals("name", option.getName());
            assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
        }
    }
}
