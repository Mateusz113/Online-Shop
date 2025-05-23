package com.mateusz113.order_service_adapters.adapter;

import com.mateusz113.order_service_adapters.client.CartServiceClient;
import com.mateusz113.order_service_adapters.mapper.ProductMapper;
import com.mateusz113.order_service_core.port.outgoing.CartServiceCommunicator;
import com.mateusz113.order_service_model.product.Customization;
import com.mateusz113.order_service_model.product.CustomizationOption;
import com.mateusz113.order_service_model.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static com.mateusz113.order_service_adapters.util.CartServiceAdaptersUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class CartServiceCommunicatorAdapterTest {
    private CartServiceClient client;
    private CartServiceCommunicator communicator;

    @BeforeEach
    void setUp() {
        ProductMapper mapper = Mappers.getMapper(ProductMapper.class);
        client = mock(CartServiceClient.class);
        communicator = new CartServiceCommunicatorAdapter(client, mapper);
    }

    @Test
    void getProductsData_ReturnsListOfProductsFromCart() {
        Long cartId = 1L;
        when(client.getCartById(cartId)).thenReturn(getCartDto());

        List<Product> resultList = communicator.getProductsData(cartId);
        
        assertEquals(2, resultList.size());
        for (Product result : resultList) {
            assertNull(result.getId());
            assertEquals(1L, result.getSourceId());
            assertEquals("name", result.getName());
            assertEquals("brand", result.getBrand());
            assertEquals(getDefaultPrice(), result.getPrice());
            assertEquals(getDefaultQuantity(), result.getQuantity());
            for (Customization customization : result.getCustomizations()) {
                assertNull(customization.getId());
                assertEquals("name", customization.getName());
                assertEquals(true, customization.getMultipleChoice());
                for (CustomizationOption customizationOption : customization.getOptions()) {
                    assertNull(customizationOption.getId());
                    assertEquals("name", customizationOption.getName());
                    assertEquals(getDefaultPriceDifference(), customizationOption.getPriceDifference());
                }
            }
        }
    }

    @Test
    void deleteCart_DeletesCart() {
        Long cartId = 1L;

        communicator.deleteCart(cartId);

        verify(client, times(1)).deleteCart(cartId);
    }
}
