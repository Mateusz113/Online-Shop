package com.mateusz113.product_service_adapters.mapper;

import com.mateusz113.product_service_adapters.entity.CustomizationElementEntity;
import com.mateusz113.product_service_adapters.entity.CustomizationOptionEntity;
import com.mateusz113.product_service_adapters.entity.ProductDetailEntity;
import com.mateusz113.product_service_adapters.entity.ProductEntity;
import com.mateusz113.product_service_model.customization.CustomizationElement;
import com.mateusz113.product_service_model.customization.CustomizationOption;
import com.mateusz113.product_service_model.product.Product;
import com.mateusz113.product_service_model.product.ProductDetail;
import com.mateusz113.product_service_model_public.commands.UpsertProductCommand;
import com.mateusz113.product_service_model_public.dto.CustomizationElementDto;
import com.mateusz113.product_service_model_public.dto.CustomizationOptionDto;
import com.mateusz113.product_service_model_public.dto.DetailedProductDto;
import com.mateusz113.product_service_model_public.dto.ProductDetailDto;
import com.mateusz113.product_service_model_public.dto.ProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static com.mateusz113.product_service_adapters.util.ProductServiceAdaptersTestUtil.*;
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
        for (ProductDetailEntity detail : result.getDetails()) {
            assertEquals(1, detail.getId());
            assertEquals("label", detail.getLabel());
            assertEquals("description", detail.getDescription());
        }
        for (CustomizationElementEntity customization : result.getCustomizations()) {
            assertEquals(1, customization.getId());
            assertEquals("name", customization.getName());
            assertTrue(customization.getMultipleChoice());
            assertNull(customization.getProduct());
            for (CustomizationOptionEntity option : customization.getOptions()) {
                assertEquals(1, option.getId());
                assertEquals("name", option.getName());
                assertFalse(option.getDefaultOption());
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
        for (ProductDetail detail : result.getDetails()) {
            assertEquals(1L, detail.getId());
            assertEquals("label", detail.getLabel());
            assertEquals("description", detail.getDescription());
        }
        for (CustomizationElement customization : result.getCustomizations()) {
            assertEquals(1, customization.getId());
            assertEquals("name", customization.getName());
            assertTrue(customization.getMultipleChoice());
            assertNull(customization.getProduct());
            for (CustomizationOption option : customization.getOptions()) {
                assertEquals(1, option.getId());
                assertEquals("name", option.getName());
                assertFalse(option.getDefaultOption());
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
    void modelToDetailedDto_ReturnsDetailedProductDto() {
        Product model = getProduct();

        DetailedProductDto result = mapper.modelToDetailedDto(model);

        assertEquals(1, result.id());
        assertEquals("name", result.name());
        assertEquals("brand", result.brand());
        assertEquals(getDefaultPrice(), result.price());
        assertEquals("type", result.type());
        assertEquals(getDefaultAvailableAmount(), result.availableAmount());
        for (ProductDetailDto detail : result.details()) {
            assertEquals(1L, detail.id());
            assertEquals("label", detail.label());
            assertEquals("description", detail.description());
        }
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
        for (ProductDetail detail : result.getDetails()) {
            assertNull(detail.getId());
            assertEquals("label", detail.getLabel());
            assertEquals("description", detail.getDescription());
        }
        for (CustomizationElement customization : result.getCustomizations()) {
            assertNull(customization.getId());
            assertEquals("name", customization.getName());
            assertTrue(customization.getMultipleChoice());
            assertNull(customization.getProduct());
            for (CustomizationOption option : customization.getOptions()) {
                assertNull(option.getId());
                assertEquals("name", option.getName());
                assertFalse(option.getDefaultOption());
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
            for (ProductDetail detail : result.getDetails()) {
                assertNull(detail.getId());
                assertEquals("label", detail.getLabel());
                assertEquals("description", detail.getDescription());
            }
            for (CustomizationElement customization : result.getCustomizations()) {
                assertNull(customization.getId());
                assertEquals("name", customization.getName());
                assertTrue(customization.getMultipleChoice());
                assertNull(customization.getProduct());
                for (CustomizationOption option : customization.getOptions()) {
                    assertNull(option.getId());
                    assertEquals("name", option.getName());
                    assertFalse(option.getDefaultOption());
                    assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
                    assertNull(option.getCustomizationElement());
                }
            }
        }
    }
}
