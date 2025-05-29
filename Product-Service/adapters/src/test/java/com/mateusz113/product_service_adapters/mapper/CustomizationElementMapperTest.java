package com.mateusz113.product_service_adapters.mapper;

import com.mateusz113.product_service_adapters.entity.CustomizationElementEntity;
import com.mateusz113.product_service_adapters.entity.CustomizationOptionEntity;
import com.mateusz113.product_service_model.customization.CustomizationElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.mateusz113.product_service_adapters.util.ProductServiceAdaptersTestUtil.getCustomizationElement;
import static com.mateusz113.product_service_adapters.util.ProductServiceAdaptersTestUtil.getDefaultPriceDifference;
import static org.junit.jupiter.api.Assertions.*;

public class CustomizationElementMapperTest {
    private CustomizationElementMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(CustomizationElementMapper.class);
    }

    @Test
    void modelToEntity_ReturnsCustomizationElementEntity() {
        CustomizationElement model = getCustomizationElement();

        CustomizationElementEntity result = mapper.modelToEntity(model);

        assertEquals(1, result.getId());
        assertEquals("name", result.getName());
        assertTrue(result.getMultipleChoice());
        assertNull(result.getProduct());
        for (CustomizationOptionEntity option : result.getOptions()) {
            assertEquals(1, option.getId());
            assertEquals("name", option.getName());
            assertFalse(option.getDefaultOption());
            assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
            assertNull(option.getCustomizationElement());
        }
    }
}
