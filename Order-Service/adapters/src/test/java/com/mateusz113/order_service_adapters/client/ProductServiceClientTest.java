package com.mateusz113.order_service_adapters.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.mateusz113.order_service_model.exception.ProductInsufficientStockException;
import com.mateusz113.order_service_model.exception.ProductNotInStockException;
import com.mateusz113.order_service_model.exception.ServiceCommunicationErrorException;
import feign.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.mateusz113.order_service_adapters.util.CartServiceAdaptersUtil.getProductsStocksMap;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableWireMock(@ConfigureWireMock(name = "product-service-client", port = 8080))
public class ProductServiceClientTest {
    @InjectWireMock("product-service-client")
    private WireMockServer mockServer;
    @Autowired
    private ProductServiceClient client;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void checkProductAvailability_ProductServiceReturnsStatus404_ThrowsProductNotInStockException() throws JsonProcessingException {
        mockServer.stubFor(get(urlPathEqualTo("/products/available-amount"))
                .withRequestBody(equalTo(objectMapper.writeValueAsString(getProductsStocksMap())))
                .willReturn(aResponse().withStatus(404)));

        ProductNotInStockException exception = assertThrows(ProductNotInStockException.class, () -> client.checkProductsAvailability(getProductsStocksMap()));

        assertEquals("Could not verify products stock as they are not present.", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void checkProductAvailability_ProductServiceReturnsStatus409_ReturnsResponseWithStatus409() throws JsonProcessingException {
        mockServer.stubFor(get(urlPathEqualTo("/products/available-amount"))
                .withRequestBody(equalTo(objectMapper.writeValueAsString(getProductsStocksMap())))
                .willReturn(aResponse().withStatus(409)));

        Response response = client.checkProductsAvailability(getProductsStocksMap());

        assertEquals(409, response.status());
    }

    @Test
    void checkProductAvailability_ProductServiceReturnsUncaughtStatus_ThrowsServiceCommunicationErrorException() throws JsonProcessingException {
        mockServer.stubFor(get(urlPathEqualTo("/products/available-amount"))
                .withRequestBody(equalTo(objectMapper.writeValueAsString(getProductsStocksMap())))
                .willReturn(serverError()));

        ServiceCommunicationErrorException exception = assertThrows(ServiceCommunicationErrorException.class, () -> client.checkProductsAvailability(getProductsStocksMap()));

        assertEquals("Could not communicate with product service.", exception.getMessage());
        assertEquals(500, exception.getStatusCode());
    }

    @Test
    void checkProductAvailability_DataIsCorrect_ReturnsResponseWithStatus204() throws JsonProcessingException {
        mockServer.stubFor(get(urlPathEqualTo("/products/available-amount"))
                .withRequestBody(equalTo(objectMapper.writeValueAsString(getProductsStocksMap())))
                .willReturn(noContent()));

        Response response = client.checkProductsAvailability(getProductsStocksMap());

        assertEquals(204, response.status());
    }

    @Test
    void updateSoldProductsStock_ProductServiceReturnsStatus404_ThrowsProductNotInStockException() throws JsonProcessingException {
        mockServer.stubFor(patch(urlPathEqualTo("/products/available-amount"))
                .withRequestBody(equalTo(objectMapper.writeValueAsString(getProductsStocksMap())))
                .willReturn(notFound()));

        ProductNotInStockException exception = assertThrows(ProductNotInStockException.class, () -> client.updateSoldProductsStock(getProductsStocksMap()));

        assertEquals("Could not update products stock as they are not present.", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void updateSoldProductsStock_ProductServiceReturnsStatus409_ThrowsProductInsufficientStockException() throws JsonProcessingException {
        mockServer.stubFor(patch(urlPathEqualTo("/products/available-amount"))
                .withRequestBody(equalTo(objectMapper.writeValueAsString(getProductsStocksMap())))
                .willReturn(status(409)));

        ProductInsufficientStockException exception = assertThrows(ProductInsufficientStockException.class, () -> client.updateSoldProductsStock(getProductsStocksMap()));

        assertEquals("Could not order products as there is not enough available.", exception.getMessage());
        assertEquals(409, exception.getStatusCode());
    }

    @Test
    void updateSoldProductsStock_ProductServiceReturnsUncaughtStatus_ThrowsServiceCommunicationErrorException() throws JsonProcessingException {
        mockServer.stubFor(patch(urlPathEqualTo("/products/available-amount"))
                .withRequestBody(equalTo(objectMapper.writeValueAsString(getProductsStocksMap())))
                .willReturn(serverError()));

        ServiceCommunicationErrorException exception = assertThrows(ServiceCommunicationErrorException.class, () -> client.updateSoldProductsStock(getProductsStocksMap()));

        assertEquals("Could not communicate with product service.", exception.getMessage());
        assertEquals(500, exception.getStatusCode());
    }

    @Test
    void updateSoldProductsStock_DataIsCorrect_UpdatesProductsStocks() throws JsonProcessingException {
        mockServer.stubFor(patch(urlPathEqualTo("/products/available-amount"))
                .withRequestBody(equalTo(objectMapper.writeValueAsString(getProductsStocksMap())))
                .willReturn(noContent()));

        assertDoesNotThrow(() -> client.updateSoldProductsStock(getProductsStocksMap()));
    }
}
