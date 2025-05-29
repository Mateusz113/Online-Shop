package com.mateusz113.order_service_adapters.mapper;

import com.mateusz113.order_service_adapters.entity.CustomizationEntity;
import com.mateusz113.order_service_adapters.entity.CustomizationOptionEntity;
import com.mateusz113.order_service_adapters.entity.OrderEntity;
import com.mateusz113.order_service_adapters.entity.ProductEntity;
import com.mateusz113.order_service_model.order.Order;
import com.mateusz113.order_service_model.order.OrderStatus;
import com.mateusz113.order_service_model.product.Customization;
import com.mateusz113.order_service_model.product.CustomizationOption;
import com.mateusz113.order_service_model.product.Product;
import com.mateusz113.order_service_model_public.command.InsertOrderCommand;
import com.mateusz113.order_service_model_public.dto.CustomizationDto;
import com.mateusz113.order_service_model_public.dto.CustomizationOptionDto;
import com.mateusz113.order_service_model_public.dto.OrderDto;
import com.mateusz113.order_service_model_public.dto.ProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getDefaultPrice;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getDefaultPriceDifference;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getDefaultQuantity;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getDefaultTime;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getInsertOrderCommand;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getOrder;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getOrderEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class OrderMapperTest {
    private OrderMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(OrderMapper.class);
    }

    @Test
    void modelToEntity_ReturnsCorrectEntityInstance() {
        Order model = getOrder();

        OrderEntity result = mapper.modelToEntity(model);

        assertEquals(1L, result.getId());
        assertEquals(1L, result.getCartId());
        assertEquals(1L, result.getClientId());
        assertEquals("clientComment", result.getClientComment());
        assertEquals(getDefaultTime(), result.getPlacementTime());
        assertEquals(OrderStatus.PENDING, result.getOrderStatus());
        assertNull(result.getInvoice());
        for (ProductEntity product : result.getProducts()) {
            assertEquals(1L, product.getId());
            assertEquals(1L, product.getSourceId());
            assertEquals("name", product.getName());
            assertEquals("brand", product.getBrand());
            assertEquals(getDefaultPrice(), product.getPrice());
            assertEquals(getDefaultQuantity(), product.getQuantity());
            for (CustomizationEntity customization : product.getCustomizations()) {
                assertEquals(1L, customization.getId());
                assertEquals("name", customization.getName());
                assertEquals(true, customization.getMultipleChoice());
                for (CustomizationOptionEntity option : customization.getOptions()) {
                    assertEquals(1L, option.getId());
                    assertEquals("name", option.getName());
                    assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
                }
            }
        }
    }

    @Test
    void entityToModel_ReturnsCorrectModelInstance() {
        OrderEntity entity = getOrderEntity();

        Order result = mapper.entityToModel(entity);

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
                for (CustomizationOption option : customization.getOptions()) {
                    assertEquals(1L, option.getId());
                    assertEquals("name", option.getName());
                    assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
                }
            }
        }
    }

    @Test
    void commandToModel_ReturnsModelInstance() {
        InsertOrderCommand command = getInsertOrderCommand();

        Order result = mapper.commandToModel(command);

        assertNull(result.getId());
        assertEquals(1L, result.getCartId());
        assertEquals(1L, result.getClientId());
        assertEquals("clientComment", result.getClientComment());
        assertNull(result.getPlacementTime());
        assertNull(result.getOrderStatus());
        assertNull(result.getInvoice());
        assertNull(result.getProducts());
    }

    @Test
    void modelToDto_ReturnsCorrectDtoInstance() {
        Order model = getOrder();

        OrderDto result = mapper.modelToDto(model);

        assertEquals(1L, result.id());
        assertEquals(1L, result.cartId());
        assertEquals(1L, result.clientId());
        assertEquals("clientComment", result.clientComment());
        assertEquals(getDefaultTime(), result.placementTime());
        assertEquals("PENDING", result.orderStatus());
        for (ProductDto product : result.products()) {
            assertEquals(1L, product.id());
            assertEquals(1L, product.sourceId());
            assertEquals("name", product.name());
            assertEquals("brand", product.brand());
            assertEquals(getDefaultPrice(), product.price());
            assertEquals(getDefaultQuantity(), product.quantity());
            for (CustomizationDto customization : product.customizations()) {
                assertEquals(1L, customization.id());
                assertEquals("name", customization.name());
                assertEquals(true, customization.multipleChoice());
                for (CustomizationOptionDto option : customization.options()) {
                    assertEquals(1L, option.id());
                    assertEquals("name", option.name());
                    assertEquals(getDefaultPriceDifference(), option.priceDifference());
                }
            }
        }
    }
}
