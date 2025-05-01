package com.mateusz113.product_service_adapters.adapter;

import com.mateusz113.product_service_adapters.arument_matcher.ProductEntityArgumentMatcher;
import com.mateusz113.product_service_adapters.entity.ProductEntity;
import com.mateusz113.product_service_adapters.mapper.ProductMapper;
import com.mateusz113.product_service_adapters.repository.ProductEntityRepository;
import com.mateusz113.product_service_model.content_management.PageableContent;
import com.mateusz113.product_service_model.customization.CustomizationElement;
import com.mateusz113.product_service_model.customization.CustomizationOption;
import com.mateusz113.product_service_model.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static com.mateusz113.product_service_adapters.util.ProductServiceTestUtil.*;
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
        for (int i = 0; i < 2; i++) {
            CustomizationElement element = result.getCustomizations().get(i);
            assertEquals(1L, element.getId());
            assertEquals("name", element.getName());
            assertTrue(element.isMultipleChoice());
            assertNull(element.getProduct());
            for (int j = 0; i < 2; i++) {
                CustomizationOption option = element.getOptions().get(j);
                assertEquals(1L, option.getId());
                assertEquals("name", option.getName());
                assertFalse(option.isDefaultOption());
                assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
                assertNull(option.getCustomizationElement());
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
                    for (int i = 0; i < 2; i++) {
                        CustomizationElement element = product.getCustomizations().get(i);
                        assertEquals(1L, element.getId());
                        assertEquals("name", element.getName());
                        assertTrue(element.isMultipleChoice());
                        assertNull(element.getProduct());
                        for (int j = 0; i < 2; i++) {
                            CustomizationOption option = element.getOptions().get(j);
                            assertEquals(1L, option.getId());
                            assertEquals("name", option.getName());
                            assertFalse(option.isDefaultOption());
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
        when(repository.findAll(pageable)).thenReturn(getPageOfProductEntity(pageNumber, pageSize));

        PageableContent<Product> result = adapter.findAll(pageNumber, pageSize);

        assertEquals(1, result.totalPages());
        assertEquals(2, result.totalElements());
        assertEquals(pageNumber, result.pageNumber());
        assertEquals(pageSize, result.pageSize());
        for (int i = 0; i < 2; i++) {
            Product product = result.elements().get(i);
            assertEquals(1L, product.getId());
            assertEquals("name", product.getName());
            assertEquals("brand", product.getBrand());
            assertEquals(getDefaultPrice(), product.getPrice());
            assertEquals("type", product.getType());
            assertEquals(getDefaultAvailableAmount(), product.getAvailableAmount());
            for (int j = 0; i < 2; i++) {
                CustomizationElement element = product.getCustomizations().get(j);
                assertEquals(1L, element.getId());
                assertEquals("name", element.getName());
                assertTrue(element.isMultipleChoice());
                assertNull(element.getProduct());
                for (int k = 0; i < 2; i++) {
                    CustomizationOption option = element.getOptions().get(k);
                    assertEquals(1L, option.getId());
                    assertEquals("name", option.getName());
                    assertFalse(option.isDefaultOption());
                    assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
                    assertNull(option.getCustomizationElement());
                }
            }
        }
    }

    @Test
    void findByType_ReturnsPageableContentOfProductsWithGivenType() {
        String type = "type";
        int pageNumber = 0;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        when(repository.findByType(type, pageable)).thenReturn(getPageOfProductEntity(pageNumber, pageSize));

        PageableContent<Product> result = adapter.findByType(type, pageNumber, pageSize);

        assertEquals(1, result.totalPages());
        assertEquals(2, result.totalElements());
        assertEquals(pageNumber, result.pageNumber());
        assertEquals(pageSize, result.pageSize());
        for (int i = 0; i < 2; i++) {
            Product product = result.elements().get(i);
            assertEquals(1L, product.getId());
            assertEquals("name", product.getName());
            assertEquals("brand", product.getBrand());
            assertEquals(getDefaultPrice(), product.getPrice());
            assertEquals("type", product.getType());
            assertEquals(getDefaultAvailableAmount(), product.getAvailableAmount());
            for (int j = 0; i < 2; i++) {
                CustomizationElement element = product.getCustomizations().get(j);
                assertEquals(1L, element.getId());
                assertEquals("name", element.getName());
                assertTrue(element.isMultipleChoice());
                assertNull(element.getProduct());
                for (int k = 0; i < 2; i++) {
                    CustomizationOption option = element.getOptions().get(k);
                    assertEquals(1L, option.getId());
                    assertEquals("name", option.getName());
                    assertFalse(option.isDefaultOption());
                    assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
                    assertNull(option.getCustomizationElement());
                }
            }
        }
    }
}
