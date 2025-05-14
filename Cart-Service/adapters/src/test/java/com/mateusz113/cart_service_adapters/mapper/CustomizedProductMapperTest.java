package com.mateusz113.cart_service_adapters.mapper;

import com.mateusz113.cart_service_adapters.entity.AppliedCustomizationEntity;
import com.mateusz113.cart_service_adapters.entity.AppliedCustomizationOptionEntity;
import com.mateusz113.cart_service_adapters.entity.CustomizedProductEntity;
import com.mateusz113.cart_service_model.customization.AppliedCustomization;
import com.mateusz113.cart_service_model.customization.AppliedCustomizationOption;
import com.mateusz113.cart_service_model.product.CustomizedProduct;
import com.mateusz113.cart_service_model_public.command.InsertCustomizedProductCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.mateusz113.cart_service_adapters.util.CartServiceAdaptersTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CustomizedProductMapperTest {
    private CustomizedProductMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(CustomizedProductMapper.class);
    }

    @Test
    void modelToEntity_ReturnsCorrectCustomizedProductEntity() {
        CustomizedProduct model = getCustomizedProduct();

        CustomizedProductEntity result = mapper.modelToEntity(model);

        assertEquals(1L, result.getId());
        assertEquals(1L, result.getSourceId());
        assertEquals("name", result.getName());
        assertEquals("brand", result.getBrand());
        assertEquals(getDefaultPrice(), result.getPrice());
        assertEquals(getDefaultQuantity(), result.getQuantity());
        assertNull(result.getCart());
        assertEquals(2, result.getAppliedCustomizations().size());
        for (AppliedCustomizationEntity customization : result.getAppliedCustomizations()) {
            assertEquals(1L, customization.getId());
            assertEquals(1L, customization.getSourceId());
            assertEquals("name", customization.getName());
            assertEquals(true, customization.getMultipleChoice());
            assertNull(customization.getProduct());
            assertEquals(2, customization.getAppliedOptions().size());
            for (AppliedCustomizationOptionEntity option : customization.getAppliedOptions()) {
                assertEquals(1L, option.getId());
                assertEquals(1L, option.getSourceId());
                assertEquals("name", option.getName());
                assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
                assertNull(option.getCustomization());
            }
        }
    }
    @Test
    void entityToModel_ReturnsCorrectCustomizedProduct() {
        CustomizedProductEntity entity = getCustomizedProductEntity();

        CustomizedProduct result = mapper.entityToModel(entity);

        assertEquals(1L, result.getId());
        assertEquals(1L, result.getSourceId());
        assertEquals("name", result.getName());
        assertEquals("brand", result.getBrand());
        assertEquals(getDefaultPrice(), result.getPrice());
        assertEquals(getDefaultQuantity(), result.getQuantity());
        assertNull(result.getCart());
        assertEquals(2, result.getAppliedCustomizations().size());
        for (AppliedCustomization customization : result.getAppliedCustomizations()) {
            assertEquals(1L, customization.getId());
            assertEquals(1L, customization.getSourceId());
            assertEquals("name", customization.getName());
            assertEquals(true, customization.getMultipleChoice());
            assertNull(customization.getProduct());
            assertEquals(2, customization.getAppliedOptions().size());
            for (AppliedCustomizationOption option : customization.getAppliedOptions()) {
                assertEquals(1L, option.getId());
                assertEquals(1L, option.getSourceId());
                assertEquals("name", option.getName());
                assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
                assertNull(option.getCustomization());
            }
        }
    }
    @Test
    void commandToModel_ReturnsCorrectCustomizedProduct() {
        InsertCustomizedProductCommand command = getInsertCustomizedProductCommand();

        CustomizedProduct result = mapper.commandToModel(command);

        assertNull(result.getId());
        assertEquals(1L, result.getSourceId());
        assertNull(result.getName());
        assertNull(result.getBrand());
        assertNull(result.getPrice());
        assertEquals(getDefaultQuantity(), result.getQuantity());
        assertNull(result.getCart());
        assertEquals(2, result.getAppliedCustomizations().size());
        for (AppliedCustomization customization : result.getAppliedCustomizations()) {
            assertNull(customization.getId());
            assertEquals(1L, customization.getSourceId());
            assertNull(customization.getName());
            assertNull(customization.getMultipleChoice());
            assertNull(customization.getProduct());
            assertEquals(2, customization.getAppliedOptions().size());
            for (AppliedCustomizationOption option : customization.getAppliedOptions()) {
                assertNull(option.getId());
                assertEquals(1L, option.getSourceId());
                assertNull(option.getName());
                assertNull(option.getPriceDifference());
                assertNull(option.getCustomization());
            }
        }
    }
}
