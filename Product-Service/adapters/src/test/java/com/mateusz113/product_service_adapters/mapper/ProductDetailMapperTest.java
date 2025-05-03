package com.mateusz113.product_service_adapters.mapper;

import com.mateusz113.product_service_adapters.entity.ProductDetailEntity;
import com.mateusz113.product_service_model.product.ProductDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.mateusz113.product_service_adapters.util.ProductServiceAdaptersTestUtil.getProductDetail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ProductDetailMapperTest {
    private ProductDetailMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ProductDetailMapper.class);
    }

    @Test
    void modelToEntity_ReturnsProductDetailEntity() {
        ProductDetail model = getProductDetail();

        ProductDetailEntity result = mapper.modelToEntity(model);

        assertEquals(1, result.getId());
        assertEquals("label", result.getLabel());
        assertEquals("description", result.getDescription());
        assertNull(result.getProduct());
    }
}
