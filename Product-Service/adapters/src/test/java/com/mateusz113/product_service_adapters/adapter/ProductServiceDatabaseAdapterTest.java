package com.mateusz113.product_service_adapters.adapter;

import com.mateusz113.product_service_adapters.arument_matcher.ProductEntityArgumentMatcher;
import com.mateusz113.product_service_adapters.entity.ProductEntity;
import com.mateusz113.product_service_adapters.mapper.ProductMapper;
import com.mateusz113.product_service_adapters.repository.ProductEntityRepository;
import com.mateusz113.product_service_model.content_management.PageableContent;
import com.mateusz113.product_service_model.customization.CustomizationElement;
import com.mateusz113.product_service_model.customization.CustomizationOption;
import com.mateusz113.product_service_model.filter.ProductFilter;
import com.mateusz113.product_service_model.product.Product;
import com.mateusz113.product_service_model.product.ProductDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

import static com.mateusz113.product_service_adapters.specification.ProductSpecification.getSpecificationFromFilter;
import static com.mateusz113.product_service_adapters.util.ProductServiceAdaptersTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceDatabaseAdapterTest {
    private ProductServiceDatabaseAdapter adapter;
    private ProductEntityRepository repository;

    @BeforeEach
    void setUp() {
        repository = mock(ProductEntityRepository.class);
        ProductMapper mapper = Mappers.getMapper(ProductMapper.class);
        adapter = new ProductServiceDatabaseAdapter(repository, mapper);
    }

    @Test
    void save_SavesAndReturnsSavedObject() {
        Product productToSave = getProduct();
        ProductEntity entityToSave = getProductEntity();
        when(repository.save(entityToSave)).thenReturn(entityToSave);

        Product result = adapter.save(productToSave);

        assertEquals(1L, result.getId());
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
        for (CustomizationElement element : result.getCustomizations()) {
            assertEquals(1L, element.getId());
            assertEquals("name", element.getName());
            assertTrue(element.getMultipleChoice());
            assertNull(element.getProduct());
            for (CustomizationOption option : element.getOptions()) {
                assertEquals(1L, option.getId());
                assertEquals("name", option.getName());
                assertFalse(option.getDefaultOption());
                assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
                assertNull(option.getCustomizationElement());
            }
        }
    }

    @Test
    void findById_ReturnsOptionalOfProduct() {
        Long idToFind = 1L;
        when(repository.findById(idToFind)).thenReturn(Optional.of(getProductEntity()));

        Optional<Product> result = adapter.findById(idToFind);

        result.ifPresentOrElse(
                product -> {
                    assertEquals(1L, product.getId());
                    assertEquals("name", product.getName());
                    assertEquals("brand", product.getBrand());
                    assertEquals(getDefaultPrice(), product.getPrice());
                    assertEquals("type", product.getType());
                    assertEquals(getDefaultAvailableAmount(), product.getAvailableAmount());
                    for (ProductDetail detail : product.getDetails()) {
                        assertEquals(1L, detail.getId());
                        assertEquals("label", detail.getLabel());
                        assertEquals("description", detail.getDescription());
                    }
                    for (CustomizationElement element : product.getCustomizations()) {
                        assertEquals(1L, element.getId());
                        assertEquals("name", element.getName());
                        assertTrue(element.getMultipleChoice());
                        assertNull(element.getProduct());
                        for (CustomizationOption option : element.getOptions()) {
                            assertEquals(1L, option.getId());
                            assertEquals("name", option.getName());
                            assertFalse(option.getDefaultOption());
                            assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
                            assertNull(option.getCustomizationElement());
                        }
                    }
                },
                () -> fail("Expected product, but optional was empty.")
        );
    }

    @Test
    void findAll_ReturnsPageableContentOfProducts() {
        int pageNumber = 0;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        ProductFilter filter = getProductFilter();
        Specification<ProductEntity> specification = getSpecificationFromFilter(filter);
        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(getPageOfProductEntity(pageNumber, pageSize));

        PageableContent<Product> result = adapter.findAll(filter, pageNumber, pageSize);

        assertEquals(1, result.totalPages());
        assertEquals(2, result.totalElements());
        assertEquals(pageNumber, result.pageNumber());
        assertEquals(pageSize, result.pageSize());
        for (Product product : result.elements()) {
            assertEquals(1L, product.getId());
            assertEquals("name", product.getName());
            assertEquals("brand", product.getBrand());
            assertEquals(getDefaultPrice(), product.getPrice());
            assertEquals("type", product.getType());
            assertEquals(getDefaultAvailableAmount(), product.getAvailableAmount());
            for (ProductDetail detail : product.getDetails()) {
                assertEquals(1L, detail.getId());
                assertEquals("label", detail.getLabel());
                assertEquals("description", detail.getDescription());
            }
            for (CustomizationElement element : product.getCustomizations()) {
                assertEquals(1L, element.getId());
                assertEquals("name", element.getName());
                assertTrue(element.getMultipleChoice());
                assertNull(element.getProduct());
                for (CustomizationOption option : element.getOptions()) {
                    assertEquals(1L, option.getId());
                    assertEquals("name", option.getName());
                    assertFalse(option.getDefaultOption());
                    assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
                    assertNull(option.getCustomizationElement());
                }
            }
        }
    }

    @Test
    void delete_DeletesProduct() {
        Product productToDelete = getProduct();
        ProductEntityArgumentMatcher argumentMatcher = new ProductEntityArgumentMatcher(getProductEntity());

        adapter.delete(productToDelete);

        verify(repository, times(1)).delete(argThat(argumentMatcher));
    }
}
