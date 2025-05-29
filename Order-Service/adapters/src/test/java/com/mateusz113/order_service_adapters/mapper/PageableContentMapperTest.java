package com.mateusz113.order_service_adapters.mapper;

import com.mateusz113.order_service_model.content.PageableContent;
import com.mateusz113.order_service_model.order.Order;
import com.mateusz113.order_service_model_public.dto.CustomizationDto;
import com.mateusz113.order_service_model_public.dto.CustomizationOptionDto;
import com.mateusz113.order_service_model_public.dto.OrderDto;
import com.mateusz113.order_service_model_public.dto.PageableContentDto;
import com.mateusz113.order_service_model_public.dto.ProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getDefaultPrice;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getDefaultPriceDifference;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getDefaultQuantity;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getDefaultTime;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getPageableContent;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PageableContentMapperTest {
    private PageableContentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(PageableContentMapper.class);
    }

    @Test
    void modelToDto_ReturnsCorrectDtoInstance() {
        PageableContent<Order> model = getPageableContent();

        PageableContentDto<OrderDto> result = mapper.modelToDto(model);

        assertEquals(1, result.totalPages());
        assertEquals(2, result.totalElements());
        assertEquals(0, result.pageNumber());
        assertEquals(2, result.pageSize());
        assertEquals(2, result.elements().size());
        for (OrderDto order : result.elements()) {
            assertEquals(1L, order.id());
            assertEquals(1L, order.cartId());
            assertEquals(1L, order.clientId());
            assertEquals("clientComment", order.clientComment());
            assertEquals(getDefaultTime(), order.placementTime());
            assertEquals("PENDING", order.orderStatus());
            for (ProductDto product : order.products()) {
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
}
