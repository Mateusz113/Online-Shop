package com.mateusz113.order_service_adapters.adapter;

import com.mateusz113.order_service_adapters.client.ProductServiceClient;
import com.mateusz113.order_service_core.port.outgoing.ProductServiceCommunicator;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ProductServiceCommunicatorAdapterTest {
    private ProductServiceCommunicator communicator;
    private ProductServiceClient client;

    @BeforeEach
    void setUp() {
        client = mock(ProductServiceClient.class);
        communicator = new ProductServiceCommunicatorAdapter(client);
    }

    @Test
    void areProductsInStock_ProductsAreAvailable_ReturnsTrue() {
        Map<Long, Integer> productsStockMap = Map.of(1L, 5, 2L, 3);
        Response response = mock(Response.class);
        when(response.status()).thenReturn(404);
        when(client.checkProductsAvailability(productsStockMap)).thenReturn(response);

        boolean result = communicator.areProductsInStock(productsStockMap);

        assertFalse(result);
    }

    @Test
    void areProductsInStock_ProductsAreNotAvailable_ReturnsTrue() {
        Map<Long, Integer> productsStockMap = Map.of(1L, 5, 2L, 3);
        Response response = mock(Response.class);
        when(response.status()).thenReturn(204);
        when(client.checkProductsAvailability(productsStockMap)).thenReturn(response);

        boolean result = communicator.areProductsInStock(productsStockMap);

        assertTrue(result);
    }

    @Test
    void updateSoldProductsStock_UpdatesProductsStock() {
        Map<Long, Integer> productsStockMap = Map.of(1L, 5, 2L, 3);

        communicator.updateSoldProductsStock(productsStockMap);

        verify(client, times(1)).updateSoldProductsStock(productsStockMap);
    }
}
