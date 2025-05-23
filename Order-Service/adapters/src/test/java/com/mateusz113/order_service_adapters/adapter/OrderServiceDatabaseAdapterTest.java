package com.mateusz113.order_service_adapters.adapter;

import com.mateusz113.order_service_adapters.entity.OrderEntity;
import com.mateusz113.order_service_adapters.mapper.OrderMapper;
import com.mateusz113.order_service_adapters.repository.OrderEntityRepository;
import com.mateusz113.order_service_core.port.outgoing.OrderServiceDatabase;
import com.mateusz113.order_service_model.content.PageableContent;
import com.mateusz113.order_service_model.order.Order;
import com.mateusz113.order_service_model.order.OrderStatus;
import com.mateusz113.order_service_model.product.Customization;
import com.mateusz113.order_service_model.product.CustomizationOption;
import com.mateusz113.order_service_model.product.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.mateusz113.order_service_adapters.util.CartServiceAdaptersUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceDatabaseAdapterTest {
    private OrderServiceDatabase database;
    private OrderEntityRepository repository;
    private OrderMapper mapper;

    @BeforeEach
    void setUp() {
        repository = mock(OrderEntityRepository.class);
        mapper = Mappers.getMapper(OrderMapper.class);
        database = new OrderServiceDatabaseAdapter(repository, mapper);
    }

    @Test
    void save_ReturnsSavedOrder() {
        Order orderToSave = getOrder();
        OrderEntity entitySaved = mapper.modelToEntity(orderToSave);
        when(repository.save(entitySaved)).thenReturn(entitySaved);

        Order result = database.save(orderToSave);

        assertEquals(1L, result.getId());
        assertEquals(1L, result.getCartId());
        assertEquals(1L, result.getClientId());
        assertEquals("clientComment", result.getClientComment());
        assertEquals(getDefaultTime(), result.getPlacementTime());
        assertEquals(OrderStatus.PENDING, result.getOrderStatus());
        assertNull(result.getInvoice());
        for (Product product : result.getProducts()) {
            assertEquals(1L, product.getId());
            assertEquals(1L, product.getSourceId());
            assertEquals("name", product.getName());
            assertEquals("brand", product.getBrand());
            assertEquals(getDefaultPrice(), product.getPrice());
            assertEquals(getDefaultQuantity(), product.getQuantity());
            for (Customization customization : product.getCustomizations()) {
                assertEquals(1L, customization.getId());
                assertEquals("name", customization.getName());
                assertEquals(true, customization.getMultipleChoice());
                for (CustomizationOption customizationOption : customization.getOptions()) {
                    assertEquals(1L, customizationOption.getId());
                    assertEquals("name", customizationOption.getName());
                    assertEquals(getDefaultPriceDifference(), customizationOption.getPriceDifference());
                }
            }
        }
    }

    @Test
    void findById_OrderExists_ReturnsOptionalWithOrder() {
        Long orderId = 1L;
        Order order = getOrder();
        OrderEntity entityToReturn = mapper.modelToEntity(order);
        when(repository.findById(orderId)).thenReturn(Optional.of(entityToReturn));

        Optional<Order> resultOptional = database.findById(orderId);

        resultOptional.ifPresentOrElse(
                result -> {
                    assertEquals(1L, result.getId());
                    assertEquals(1L, result.getCartId());
                    assertEquals(1L, result.getClientId());
                    assertEquals("clientComment", result.getClientComment());
                    assertEquals(getDefaultTime(), result.getPlacementTime());
                    assertEquals(OrderStatus.PENDING, result.getOrderStatus());
                    assertNull(result.getInvoice());
                    for (Product product : result.getProducts()) {
                        assertEquals(1L, product.getId());
                        assertEquals(1L, product.getSourceId());
                        assertEquals("name", product.getName());
                        assertEquals("brand", product.getBrand());
                        assertEquals(getDefaultPrice(), product.getPrice());
                        assertEquals(getDefaultQuantity(), product.getQuantity());
                        for (Customization customization : product.getCustomizations()) {
                            assertEquals(1L, customization.getId());
                            assertEquals("name", customization.getName());
                            assertEquals(true, customization.getMultipleChoice());
                            for (CustomizationOption customizationOption : customization.getOptions()) {
                                assertEquals(1L, customizationOption.getId());
                                assertEquals("name", customizationOption.getName());
                                assertEquals(getDefaultPriceDifference(), customizationOption.getPriceDifference());
                            }
                        }
                    }
                },
                Assertions::fail
        );
    }

    @Test
    void findById_OrderDoesNotExist_ReturnsEmptyOptional() {
        Long orderId = 1L;
        when(repository.findById(orderId)).thenReturn(Optional.empty());

        Optional<Order> resultOptional = database.findById(orderId);

        assertTrue(resultOptional.isEmpty());
    }

    @Test
    void findAll_ReturnsPageableContentOfOrder() {
        Pageable pageable = PageRequest.of(0, 2);
        OrderEntity orderEntity = mapper.modelToEntity(getOrder());
        Page<OrderEntity> entityPage = new PageImpl<>(List.of(orderEntity, orderEntity), pageable, 2);
        when(repository.findAll(pageable)).thenReturn(entityPage);

        PageableContent<Order> result = database.findAll(pageable.getPageNumber(), pageable.getPageSize());

        assertEquals(1, result.totalPages());
        assertEquals(2, result.totalElements());
        assertEquals(0, result.pageNumber());
        assertEquals(2, result.pageSize());
        for (Order order : result.elements()) {
            assertEquals(1L, order.getId());
            assertEquals(1L, order.getCartId());
            assertEquals(1L, order.getClientId());
            assertEquals("clientComment", order.getClientComment());
            assertEquals(getDefaultTime(), order.getPlacementTime());
            assertEquals(OrderStatus.PENDING, order.getOrderStatus());
            assertNull(order.getInvoice());
            for (Product product : order.getProducts()) {
                assertEquals(1L, product.getId());
                assertEquals(1L, product.getSourceId());
                assertEquals("name", product.getName());
                assertEquals("brand", product.getBrand());
                assertEquals(getDefaultPrice(), product.getPrice());
                assertEquals(getDefaultQuantity(), product.getQuantity());
                for (Customization customization : product.getCustomizations()) {
                    assertEquals(1L, customization.getId());
                    assertEquals("name", customization.getName());
                    assertEquals(true, customization.getMultipleChoice());
                    for (CustomizationOption customizationOption : customization.getOptions()) {
                        assertEquals(1L, customizationOption.getId());
                        assertEquals("name", customizationOption.getName());
                        assertEquals(getDefaultPriceDifference(), customizationOption.getPriceDifference());
                    }
                }
            }
        }
    }

    @Test
    void findByClientId_ReturnsPageableContentOfOrder() {
        Pageable pageable = PageRequest.of(0, 2);
        Long clientId = 1L;
        OrderEntity orderEntity = mapper.modelToEntity(getOrder());
        Page<OrderEntity> entityPage = new PageImpl<>(List.of(orderEntity, orderEntity), pageable, 2);
        when(repository.findAllByClientId(clientId, pageable)).thenReturn(entityPage);

        PageableContent<Order> result = database.findByClientId(clientId, pageable.getPageNumber(), pageable.getPageSize());

        assertEquals(1, result.totalPages());
        assertEquals(2, result.totalElements());
        assertEquals(0, result.pageNumber());
        assertEquals(2, result.pageSize());
        for (Order order : result.elements()) {
            assertEquals(1L, order.getId());
            assertEquals(1L, order.getCartId());
            assertEquals(1L, order.getClientId());
            assertEquals("clientComment", order.getClientComment());
            assertEquals(getDefaultTime(), order.getPlacementTime());
            assertEquals(OrderStatus.PENDING, order.getOrderStatus());
            assertNull(order.getInvoice());
            for (Product product : order.getProducts()) {
                assertEquals(1L, product.getId());
                assertEquals(1L, product.getSourceId());
                assertEquals("name", product.getName());
                assertEquals("brand", product.getBrand());
                assertEquals(getDefaultPrice(), product.getPrice());
                assertEquals(getDefaultQuantity(), product.getQuantity());
                for (Customization customization : product.getCustomizations()) {
                    assertEquals(1L, customization.getId());
                    assertEquals("name", customization.getName());
                    assertEquals(true, customization.getMultipleChoice());
                    for (CustomizationOption customizationOption : customization.getOptions()) {
                        assertEquals(1L, customizationOption.getId());
                        assertEquals("name", customizationOption.getName());
                        assertEquals(getDefaultPriceDifference(), customizationOption.getPriceDifference());
                    }
                }
            }
        }
    }

    @Test
    void delete_DeletesOrder() {
        Order orderToDelete = getOrder();
        OrderEntity deletedEntity = mapper.modelToEntity(orderToDelete);

        database.delete(orderToDelete);

        verify(repository, times(1)).delete(deletedEntity);
    }
}

