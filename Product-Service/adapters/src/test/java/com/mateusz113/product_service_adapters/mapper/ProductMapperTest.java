package com.mateusz113.product_service_adapters.mapper;

import com.mateusz113.product_service_adapters.entity.CustomizationElementEntity;
import com.mateusz113.product_service_adapters.entity.CustomizationOptionEntity;
import com.mateusz113.product_service_adapters.entity.ProductEntity;
import com.mateusz113.product_service_model.customization.CustomizationElement;
import com.mateusz113.product_service_model.customization.CustomizationOption;
import com.mateusz113.product_service_model.product.Product;
import com.mateusz113.product_service_model_public.commands.UpsertProductCommand;
import com.mateusz113.product_service_model_public.dto.CustomizationElementDto;
import com.mateusz113.product_service_model_public.dto.CustomizationOptionDto;
import com.mateusz113.product_service_model_public.dto.ProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static com.mateusz113.product_service_adapters.util.ProductServiceTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

public class ProductMapperTest {
    private ProductMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ProductMapper.class);
    }

    @Test
    void modelToEntity_ReturnsProductEntity() {
        Product model = getProduct();

        ProductEntity result = mapper.modelToEntity(model);

        assertEquals(1, result.getId());
        assertEquals("name", result.getName());
        assertEquals("brand", result.getBrand());
        assertEquals(getDefaultPrice(), result.getPrice());
        assertEquals("type", result.getType());
        assertEquals(getDefaultAvailableAmount(), result.getAvailableAmount());
        for (CustomizationElementEntity customization : result.getCustomizations()) {
            assertEquals(1, customization.getId());
            assertEquals("name", customization.getName());
            assertTrue(customization.isMultipleChoice());
            assertNull(customization.getProduct());
            for (CustomizationOptionEntity option : customization.getOptions()) {
                assertEquals(1, option.getId());
                assertEquals("name", option.getName());
                assertFalse(option.isDefaultOption());
                assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
                assertNull(option.getCustomizationElement());
            }
        }
    }

    @Test
    void entityToModel_ReturnsProduct() {
        ProductEntity entity = getProductEntity();

        Product result = mapper.entityToModel(entity);

        assertEquals(1, result.getId());
        assertEquals("name", result.getName());
        assertEquals("brand", result.getBrand());
        assertEquals(getDefaultPrice(), result.getPrice());
        assertEquals("type", result.getType());
        assertEquals(getDefaultAvailableAmount(), result.getAvailableAmount());
        for (CustomizationElement customization : result.getCustomizations()) {
            assertEquals(1, customization.getId());
            assertEquals("name", customization.getName());
            assertTrue(customization.isMultipleChoice());
            assertNull(customization.getProduct());
            for (CustomizationOption option : customization.getOptions()) {
                assertEquals(1, option.getId());
                assertEquals("name", option.getName());
                assertFalse(option.isDefaultOption());
                assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
                assertNull(option.getCustomizationElement());
            }
        }
    }

    @Test
    void modelToDto_ReturnsProductDto() {
        Product model = getProduct();

        ProductDto result = mapper.modelToDto(model);

        assertEquals(1, result.id());
        assertEquals("name", result.name());
        assertEquals("brand", result.brand());
        assertEquals(getDefaultPrice(), result.price());
        assertEquals("type", result.type());
        assertEquals(getDefaultAvailableAmount(), result.availableAmount());
        for (CustomizationElementDto customization : result.customizations()) {
            assertEquals(1, customization.id());
            assertEquals("name", customization.name());
            assertTrue(customization.multipleChoice());
            for (CustomizationOptionDto option : customization.options()) {
                assertEquals(1, option.id());
                assertEquals("name", option.name());
                assertFalse(option.defaultOption());
                assertEquals(getDefaultPriceDifference(), option.priceDifference());
            }
        }
    }

    @Test
    void modelListToDtoList_ReturnsProductDtoList() {
        List<Product> modelList = List.of(getProduct(), getProduct());

        List<ProductDto> resultList = modelList.stream()
                .map(mapper::modelToDto)
                .toList();

        for (ProductDto result : resultList) {
            assertEquals(1, result.id());
            assertEquals("name", result.name());
            assertEquals("brand", result.brand());
            assertEquals(getDefaultPrice(), result.price());
            assertEquals("type", result.type());
            assertEquals(getDefaultAvailableAmount(), result.availableAmount());
            for (CustomizationElementDto customization : result.customizations()) {
                assertEquals(1, customization.id());
                assertEquals("name", customization.name());
                assertTrue(customization.multipleChoice());
                for (CustomizationOptionDto option : customization.options()) {
                    assertEquals(1, option.id());
                    assertEquals("name", option.name());
                    assertFalse(option.defaultOption());
                    assertEquals(getDefaultPriceDifference(), option.priceDifference());
                }
            }
        }
    }

    @Test
    void commandToModel_ReturnsProduct() {
        UpsertProductCommand command = getUpsertProductCommand();

        Product result = mapper.commandToModel(command);

        assertNull(result.getId());
        assertEquals("name", result.getName());
        assertEquals("brand", result.getBrand());
        assertEquals(getDefaultPrice(), result.getPrice());
        assertEquals("type", result.getType());
        assertEquals(getDefaultAvailableAmount(), result.getAvailableAmount());
        for (CustomizationElement customization : result.getCustomizations()) {
            assertNull(customization.getId());
            assertEquals("name", customization.getName());
            assertTrue(customization.isMultipleChoice());
            assertNull(customization.getProduct());
            for (CustomizationOption option : customization.getOptions()) {
                assertNull(option.getId());
                assertEquals("name", option.getName());
                assertFalse(option.isDefaultOption());
                assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
                assertNull(option.getCustomizationElement());
            }
        }
    }

    @Test
    void commandListToModelList_ReturnsProductList() {
        List<UpsertProductCommand> commandList = List.of(getUpsertProductCommand(), getUpsertProductCommand());

        List<Product> resultList = commandList.stream()
                .map(mapper::commandToModel)
                .toList();

        for (Product result : resultList) {
            assertNull(result.getId());
            assertEquals("name", result.getName());
            assertEquals("brand", result.getBrand());
            assertEquals(getDefaultPrice(), result.getPrice());
            assertEquals("type", result.getType());
            assertEquals(getDefaultAvailableAmount(), result.getAvailableAmount());
            for (CustomizationElement customization : result.getCustomizations()) {
                assertNull(customization.getId());
                assertEquals("name", customization.getName());
                assertTrue(customization.isMultipleChoice());
                assertNull(customization.getProduct());
                for (CustomizationOption option : customization.getOptions()) {
                    assertNull(option.getId());
                    assertEquals("name", option.getName());
                    assertFalse(option.isDefaultOption());
                    assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
                    assertNull(option.getCustomizationElement());
                }
            }
        }
    }
}
