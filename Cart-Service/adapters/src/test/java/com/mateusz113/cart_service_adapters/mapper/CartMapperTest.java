package com.mateusz113.cart_service_adapters.mapper;

import com.mateusz113.cart_service_adapters.entity.AppliedCustomizationEntity;
import com.mateusz113.cart_service_adapters.entity.AppliedCustomizationOptionEntity;
import com.mateusz113.cart_service_adapters.entity.CartEntity;
import com.mateusz113.cart_service_adapters.entity.CustomizedProductEntity;
import com.mateusz113.cart_service_model.cart.Cart;
import com.mateusz113.cart_service_model.customization.AppliedCustomization;
import com.mateusz113.cart_service_model.customization.AppliedCustomizationOption;
import com.mateusz113.cart_service_model.product.CustomizedProduct;
import com.mateusz113.cart_service_model_public.command.InsertCartCommand;
import com.mateusz113.cart_service_model_public.dto.AppliedCustomizationDto;
import com.mateusz113.cart_service_model_public.dto.AppliedCustomizationOptionDto;
import com.mateusz113.cart_service_model_public.dto.CartDto;
import com.mateusz113.cart_service_model_public.dto.CustomizedProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.mateusz113.cart_service_adapters.util.CartServiceAdaptersTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CartMapperTest {
    private CartMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(CartMapper.class);
    }

    @Test
    void modelToEntity_ReturnsCorrectCartEntity() {
        Cart model = getCart();

        CartEntity result = mapper.modelToEntity(model);

        assertEquals(1L, result.getId());
        assertEquals(getDefaultTime(), result.getCreationDate());
        assertEquals(2, result.getCustomizedProducts().size());
        for (CustomizedProductEntity product : result.getCustomizedProducts()) {
            assertEquals(1L, product.getId());
            assertEquals(1L, product.getSourceId());
            assertEquals("name", product.getName());
            assertEquals("brand", product.getBrand());
            assertEquals(getDefaultPrice(), product.getPrice());
            assertEquals(getDefaultQuantity(), product.getQuantity());
            assertNull(product.getCart());
            assertEquals(2, product.getAppliedCustomizations().size());
            for (AppliedCustomizationEntity customization : product.getAppliedCustomizations()) {
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
    }

    @Test
    void entityToModel_ReturnsCorrectCart() {
        CartEntity entity = getCartEntity();

        Cart result = mapper.entityToModel(entity);

        assertEquals(1L, result.getId());
        assertEquals(getDefaultTime(), result.getCreationDate());
        assertEquals(2, result.getCustomizedProducts().size());
        for (CustomizedProduct product : result.getCustomizedProducts()) {
            assertEquals(1L, product.getId());
            assertEquals(1L, product.getSourceId());
            assertEquals("name", product.getName());
            assertEquals("brand", product.getBrand());
            assertEquals(getDefaultPrice(), product.getPrice());
            assertEquals(getDefaultQuantity(), product.getQuantity());
            assertNull(product.getCart());
            assertEquals(2, product.getAppliedCustomizations().size());
            for (AppliedCustomization customization : product.getAppliedCustomizations()) {
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
    }

    @Test
    void commandToModel_ReturnsCorrectCart() {
        InsertCartCommand command = getInsertCartCommand();

        Cart result = mapper.commandToModel(command);

        assertNull(result.getId());
        assertNull(result.getCreationDate());
        assertEquals(2, result.getCustomizedProducts().size());
        for (CustomizedProduct product : result.getCustomizedProducts()) {
            assertNull(product.getId());
            assertEquals(1L, product.getSourceId());
            assertNull(product.getName());
            assertNull(product.getBrand());
            assertNull(product.getPrice());
            assertEquals(getDefaultQuantity(), product.getQuantity());
            assertNull(product.getCart());
            assertEquals(2, product.getAppliedCustomizations().size());
            for (AppliedCustomization customization : product.getAppliedCustomizations()) {
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

    @Test
    void modelToDto_ReturnsCorrectCartDto() {
        Cart model = getCart();

        CartDto result = mapper.modelToDto(model);

        assertEquals(1L, result.id());
        assertEquals(getDefaultTime(), result.creationDate());
        assertEquals(2, result.customizedProducts().size());
        for (CustomizedProductDto product : result.customizedProducts()) {
            assertEquals(1L, product.id());
            assertEquals(1L, product.sourceId());
            assertEquals("name", product.name());
            assertEquals("brand", product.brand());
            assertEquals(getDefaultPrice(), product.price());
            assertEquals(getDefaultQuantity(), product.quantity());
            assertEquals(2, product.appliedCustomizations().size());
            for (AppliedCustomizationDto customization : product.appliedCustomizations()) {
                assertEquals(1L, customization.id());
                assertEquals(1L, customization.sourceId());
                assertEquals("name", customization.name());
                assertEquals(true, customization.multipleChoice());
                assertEquals(2, customization.appliedOptions().size());
                for (AppliedCustomizationOptionDto option : customization.appliedOptions()) {
                    assertEquals(1L, option.id());
                    assertEquals(1L, option.sourceId());
                    assertEquals("name", option.name());
                    assertEquals(getDefaultPriceDifference(), option.priceDifference());
                }
            }
        }
    }
}
