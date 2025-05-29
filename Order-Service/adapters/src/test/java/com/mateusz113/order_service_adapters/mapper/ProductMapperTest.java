package com.mateusz113.order_service_adapters.mapper;

import com.mateusz113.order_service_model.product.Customization;
import com.mateusz113.order_service_model.product.CustomizationOption;
import com.mateusz113.order_service_model.product.Product;
import com.mateusz113.order_service_model_public.dto.CartProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getCartProductDto;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getDefaultPrice;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getDefaultPriceDifference;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getDefaultQuantity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ProductMapperTest {
    private ProductMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ProductMapper.class);
    }

    @Test
    void dtoToModel() {
        CartProductDto dto = getCartProductDto();

        Product result = mapper.dtoToModel(dto);

        assertNull(result.getId());
        assertEquals(1L, result.getSourceId());
        assertEquals("name", result.getName());
        assertEquals("brand", result.getBrand());
        assertEquals(getDefaultPrice(), result.getPrice());
        assertEquals(getDefaultQuantity(), result.getQuantity());
        for (Customization customization : result.getCustomizations()) {
            assertNull(customization.getId());
            assertEquals("name", customization.getName());
            assertEquals(true, customization.getMultipleChoice());
            for (CustomizationOption option : customization.getOptions()) {
                assertNull(option.getId());
                assertEquals("name", option.getName());
                assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
            }
        }
    }
}
