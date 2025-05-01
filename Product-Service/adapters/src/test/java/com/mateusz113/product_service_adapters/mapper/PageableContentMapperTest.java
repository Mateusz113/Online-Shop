package com.mateusz113.product_service_adapters.mapper;

import com.mateusz113.product_service_model.content_management.PageableContent;
import com.mateusz113.product_service_model.product.Product;
import com.mateusz113.product_service_model_public.dto.CustomizationElementDto;
import com.mateusz113.product_service_model_public.dto.CustomizationOptionDto;
import com.mateusz113.product_service_model_public.dto.PageableContentDto;
import com.mateusz113.product_service_model_public.dto.ProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static com.mateusz113.product_service_adapters.util.ProductServiceTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

public class PageableContentMapperTest {
    private PageableContentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(PageableContentMapper.class);
    }

    @Test
    void modelToDto_ReturnsPageableContentDto() {
        Pageable pageable = PageRequest.of(0, 2);
        PageableContent<Product> model = getProductPageableContent(pageable);

        PageableContentDto<ProductDto> result = mapper.modelToDto(model);

        assertEquals(1, result.totalPages());
        assertEquals(2, result.totalElements());
        assertEquals(pageable.getPageNumber(), result.pageNumber());
        assertEquals(pageable.getPageSize(), result.pageSize());
        for (ProductDto product : result.elements()) {
            assertEquals(1, product.id());
            assertEquals("name", product.name());
            assertEquals("brand", product.brand());
            assertEquals(getDefaultPrice(), product.price());
            assertEquals("type", product.type());
            assertEquals(getDefaultAvailableAmount(), product.availableAmount());
            for (CustomizationElementDto customization : product.customizations()) {
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
}
