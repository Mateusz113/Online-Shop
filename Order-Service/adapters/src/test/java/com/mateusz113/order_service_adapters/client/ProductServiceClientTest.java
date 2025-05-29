package com.mateusz113.order_service_adapters.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import com.mateusz113.order_service_model.exception.ProductInsufficientStockException;
import com.mateusz113.order_service_model.exception.ProductNotInStockException;
import com.mateusz113.order_service_model.exception.ServiceCommunicationErrorException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getProductsStocksMap;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@EnableWireMock(@ConfigureWireMock(name = "product-service-client", port = 8080))
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductServiceClientTest {
    @InjectWireMock("product-service-client")
    private WireMockServer mockServer;
    @Autowired
    private ProductServiceClient client;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void checkProductAvailability_ProductServiceReturnsStatus404_ThrowsProductNotInStockException() {
        UrlPattern template = urlPathTemplate("/products/{productId}/available-amount");
        Long productId = 1L;
        Integer requiredStock = 5;
        mockServer.stubFor(get(template)
                .withPathParam("productId", equalTo(String.valueOf(productId)))
                .withQueryParam("requiredStock", equalTo(String.valueOf(requiredStock)))
                .willReturn(aResponse().withStatus(404)));

        ProductNotInStockException exception = assertThrows(ProductNotInStockException.class, () -> client.checkProductsAvailability(productId, requiredStock));


        assertEquals("Could not verify stock of product with id: 1 as they are not present.", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void checkProductAvailability_ProductServiceReturnsStatus409_ReturnsResponseWithStatus409() {
        UrlPattern template = urlPathTemplate("/products/{productId}/available-amount");
        Long productId = 1L;
        Integer requiredStock = 5;
        mockServer.stubFor(get(template)
                .withPathParam("productId", equalTo(String.valueOf(productId)))
                .withQueryParam("requiredStock", equalTo(String.valueOf(requiredStock)))
                .willReturn(aResponse().withStatus(409)));

        ResponseEntity<Void> response = client.checkProductsAvailability(productId, requiredStock);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void checkProductAvailability_ProductServiceReturnsUncaughtStatus_ThrowsServiceCommunicationErrorException() {
        UrlPattern template = urlPathTemplate("/products/{productId}/available-amount");
        Long productId = 1L;
        Integer requiredStock = 5;
        mockServer.stubFor(get(template)
                .withPathParam("productId", equalTo(String.valueOf(productId)))
                .withQueryParam("requiredStock", equalTo(String.valueOf(requiredStock)))
                .willReturn(aResponse().withStatus(500)));

        ServiceCommunicationErrorException exception = assertThrows(ServiceCommunicationErrorException.class, () -> client.checkProductsAvailability(productId, requiredStock));

        assertEquals("Could not communicate with product service.", exception.getMessage());
        assertEquals(500, exception.getStatusCode());
    }

    @Test
    void checkProductAvailability_DataIsCorrect_ReturnsResponseWithStatus204() {
        UrlPattern template = urlPathTemplate("/products/{productId}/available-amount");
        Long productId = 1L;
        Integer requiredStock = 5;
        mockServer.stubFor(get(template)
                .withPathParam("productId", equalTo(String.valueOf(productId)))
                .withQueryParam("requiredStock", equalTo(String.valueOf(requiredStock)))
                .willReturn(noContent()));

        ResponseEntity<Void> response = client.checkProductsAvailability(productId, requiredStock);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
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

    @AfterAll
    void cleanUp() {
        mockServer.stop();
    }
}
