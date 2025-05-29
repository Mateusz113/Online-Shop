package com.mateusz113.order_service_adapters.adapter;

import com.mateusz113.order_service_adapters.client.ProductServiceClient;
import com.mateusz113.order_service_core.port.outgoing.ProductServiceCommunicator;
import com.mateusz113.order_service_model.exception.ProductNotInStockException;
import com.mateusz113.order_service_model_public.command.UpdateProductsStocksCommand;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProductServiceCommunicatorAdapterTest {
    private ProductServiceCommunicator communicator;
    private ProductServiceClient client;

    @BeforeEach
    void setUp() {
        client = mock(ProductServiceClient.class);
        communicator = new ProductServiceCommunicatorAdapter(client);
    }

    @Test
    void areProductsInStock_ProductsIdsAreIncorrect_ReturnsFalse() {
        Map<Long, Integer> productsStockMap = Map.of(1L, 5, 2L, 3);
        Response response = mock(Response.class);
        when(response.status()).thenReturn(404);
        when(client.checkProductsAvailability(any(Long.class), any(Integer.class))).thenThrow(ProductNotInStockException.class);

        assertThrows(ProductNotInStockException.class, () -> communicator.areProductsInStock(productsStockMap));
    }

    @Test
    void areProductsInStock_ProductsAreNotAvailable_ReturnsTrue() {
        Map<Long, Integer> productsStockMap = Map.of(1L, 5, 2L, 3);
        when(client.checkProductsAvailability(any(Long.class), any(Integer.class))).thenReturn(ResponseEntity.status(409).build());

        boolean result = communicator.areProductsInStock(productsStockMap);

        assertFalse(result);
    }

    @Test
    void areProductsInStock_ProductsAreAvailable_ReturnsTrue() {
        Map<Long, Integer> productsStockMap = Map.of(1L, 5, 2L, 3);
        Response response = mock(Response.class);
        when(response.status()).thenReturn(204);
        when(client.checkProductsAvailability(any(Long.class), any(Integer.class))).thenReturn(ResponseEntity.noContent().build());

        boolean result = communicator.areProductsInStock(productsStockMap);

        assertTrue(result);
    }

    @Test
    void updateSoldProductsStock_UpdatesProductsStock() {
        Map<Long, Integer> productsStockMap = Map.of(1L, 5, 2L, 3);
        UpdateProductsStocksCommand command = new UpdateProductsStocksCommand(productsStockMap);

        communicator.updateSoldProductsStock(productsStockMap);

        verify(client, times(1)).updateSoldProductsStock(command);
    }
}
