package com.mateusz113.product_service_adapters.mapper;

import com.mateusz113.product_service_adapters.entity.CustomizationOptionEntity;
import com.mateusz113.product_service_model.customization.CustomizationOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.mateusz113.product_service_adapters.util.ProductServiceAdaptersTestUtil.getCustomizationOption;
import static com.mateusz113.product_service_adapters.util.ProductServiceAdaptersTestUtil.getDefaultPriceDifference;
import static org.junit.jupiter.api.Assertions.*;

public class CustomizationOptionMapperTest {
    private CustomizationOptionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(CustomizationOptionMapper.class);
    }

    @Test
    void modelToEntity_ReturnsCustomizationOptionEntity() {
        CustomizationOption model = getCustomizationOption();

        CustomizationOptionEntity result = mapper.modelToEntity(model);

        assertEquals(1, result.getId());
        assertEquals("name", result.getName());
        assertFalse(result.getDefaultOption());
        assertEquals(getDefaultPriceDifference(), result.getPriceDifference());
        assertNull(result.getCustomizationElement());
    }
}
