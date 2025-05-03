package com.mateusz113.product_service_adapters.mapper;

import com.mateusz113.product_service_model.filter.ProductFilter;
import com.mateusz113.product_service_model_public.commands.FilterProductCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.mateusz113.product_service_adapters.util.ProductServiceAdaptersTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductFilterMapperTest {
    private ProductFilterMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ProductFilterMapper.class);
    }

    @Test
    void commandToModel_ReturnsProductFilter() {
        FilterProductCommand command = getFilterProductCommand();

        ProductFilter result = mapper.commandToModel(command);

        assertEquals("name", result.name());
        assertEquals("brand", result.brand());
        assertEquals(getDefaultPrice(), result.minPrice());
        assertEquals(getDefaultPrice(), result.maxPrice());
        assertEquals("type", result.type());
        assertEquals(getDefaultAvailableAmount(), result.minAvailableAmount());
    }
}
