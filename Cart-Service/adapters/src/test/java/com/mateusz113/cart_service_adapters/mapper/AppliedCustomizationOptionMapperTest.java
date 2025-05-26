package com.mateusz113.cart_service_adapters.mapper;

import com.mateusz113.cart_service_adapters.entity.AppliedCustomizationOptionEntity;
import com.mateusz113.cart_service_model.customization.AppliedCustomizationOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.mateusz113.cart_service_adapters.util.CartServiceAdaptersTestUtil.getAppliedCustomizationOption;
import static com.mateusz113.cart_service_adapters.util.CartServiceAdaptersTestUtil.getDefaultPriceDifference;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AppliedCustomizationOptionMapperTest {
    private AppliedCustomizationOptionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(AppliedCustomizationOptionMapper.class);
    }

    @Test
    void modelToEntity_ReturnsCorrectAppliedCustomizationOptionEntity() {
        AppliedCustomizationOption model = getAppliedCustomizationOption();

        AppliedCustomizationOptionEntity result = mapper.modelToEntity(model);

        assertEquals(1L, result.getId());
        assertEquals(1L, result.getSourceId());
        assertEquals("name", result.getName());
        assertEquals(getDefaultPriceDifference(), result.getPriceDifference());
        assertNull(result.getCustomization());
    }
}
