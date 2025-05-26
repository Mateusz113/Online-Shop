package com.mateusz113.cart_service_adapters.mapper;

import com.mateusz113.cart_service_adapters.entity.AppliedCustomizationEntity;
import com.mateusz113.cart_service_adapters.entity.AppliedCustomizationOptionEntity;
import com.mateusz113.cart_service_model.customization.AppliedCustomization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.mateusz113.cart_service_adapters.util.CartServiceAdaptersTestUtil.getAppliedCustomization;
import static com.mateusz113.cart_service_adapters.util.CartServiceAdaptersTestUtil.getDefaultPriceDifference;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AppliedCustomizationMapperTest {
    private AppliedCustomizationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(AppliedCustomizationMapper.class);
    }

    @Test
    void modelToEntity_ReturnsCorrectAppliedCustomizationEntity() {
        AppliedCustomization model = getAppliedCustomization();

        AppliedCustomizationEntity result = mapper.modelToEntity(model);

        assertEquals(1L, result.getId());
        assertEquals(1L, result.getSourceId());
        assertEquals("name", result.getName());
        assertEquals(true, result.getMultipleChoice());
        assertNull(result.getProduct());
        assertEquals(2, result.getAppliedOptions().size());
        for (AppliedCustomizationOptionEntity option : result.getAppliedOptions()) {
            assertEquals(1L, option.getId());
            assertEquals(1L, option.getSourceId());
            assertEquals("name", option.getName());
            assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
            assertNull(option.getCustomization());
        }
    }
}
