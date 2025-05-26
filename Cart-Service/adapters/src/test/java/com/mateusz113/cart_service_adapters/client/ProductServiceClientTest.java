package com.mateusz113.cart_service_adapters.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import com.mateusz113.cart_service_model.customization.SourceCustomizationElement;
import com.mateusz113.cart_service_model.customization.SourceCustomizationOption;
import com.mateusz113.cart_service_model.exception.ProductServiceCommunicationErrorException;
import com.mateusz113.cart_service_model.exception.ProductServiceIsUnavailableException;
import com.mateusz113.cart_service_model.exception.SourceProductNotPresentException;
import com.mateusz113.cart_service_model.product.SourceProduct;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.mateusz113.cart_service_adapters.util.CartServiceAdaptersTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableWireMock(@ConfigureWireMock(name = "product-service-client", port = 8080))
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
public class ProductServiceClientTest {
    @InjectWireMock("product-service-client")
    private WireMockServer server;
    @Autowired
    private ProductServiceClient client;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getProductById_SourceProductDoesNotExist_ThrowsSourceProductNotPresentException() throws JsonProcessingException {
        Long productId = 1L;
        UrlPattern urlTemplate = urlPathTemplate("/products/{productId}");
        server.stubFor(get(urlTemplate)
                .withPathParam("productId", equalTo(String.valueOf(productId)))
                .willReturn(notFound()));

        SourceProductNotPresentException exception = assertThrows(SourceProductNotPresentException.class, () -> client.getProductById(productId));

        assertEquals("Source product with id: 1 does not exist.", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    public void getProductById_ProductServiceIsNotAvailable_ThrowsProductServiceIsUnavailableException() throws JsonProcessingException {
        Long productId = 1L;
        UrlPattern urlTemplate = urlPathTemplate("/products/{productId}");
        server.stubFor(get(urlTemplate)
                .withPathParam("productId", equalTo(String.valueOf(productId)))
                .willReturn(serviceUnavailable()));

        ProductServiceIsUnavailableException exception = assertThrows(ProductServiceIsUnavailableException.class, () -> client.getProductById(productId));

        assertEquals("Product service is currently unavailable.", exception.getMessage());
        assertEquals(503, exception.getStatusCode());
    }

    @Test
    public void getProductById_ProductServiceReturnsUncoveredStatus_ThrowsProductServiceCommunicationErrorException() throws JsonProcessingException {
        Long productId = 1L;
        UrlPattern urlTemplate = urlPathTemplate("/products/{productId}");
        server.stubFor(get(urlTemplate)
                .withPathParam("productId", equalTo(String.valueOf(productId)))
                .willReturn(serverError()));

        ProductServiceCommunicationErrorException exception = assertThrows(ProductServiceCommunicationErrorException.class, () -> client.getProductById(productId));

        assertEquals("Could not communicate with service to get source data.", exception.getMessage());
        assertEquals(500, exception.getStatusCode());
    }

    @Test
    public void getProductById_SourceProductExists_ReturnsSourceProduct() throws JsonProcessingException {
        Long productId = 1L;
        SourceProduct sourceProduct = getSourceProduct();
        String response = objectMapper.writeValueAsString(sourceProduct);
        UrlPattern urlTemplate = urlPathTemplate("/products/{productId}");
        server.stubFor(get(urlTemplate)
                .withPathParam("productId", equalTo(String.valueOf(productId)))
                .willReturn(okJson(response)));

        SourceProduct result = client.getProductById(productId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("source_name", result.getName());
        assertEquals("source_brand", result.getBrand());
        assertEquals(getDefaultPrice(), result.getPrice());
        assertEquals(getDefaultAvailableAmount(), result.getAvailableAmount());
        assertEquals(2, result.getCustomizations().size());
        for (SourceCustomizationElement customizationElement : result.getCustomizations()) {
            assertEquals(1L, customizationElement.getId());
            assertEquals("source_name", customizationElement.getName());
            assertEquals(true, customizationElement.getMultipleChoice());
            assertEquals(2, customizationElement.getOptions().size());
            for (SourceCustomizationOption customizationOption : customizationElement.getOptions()) {
                assertEquals(1L, customizationOption.getId());
                assertEquals("source_name", customizationOption.getName());
                assertEquals(getDefaultPriceDifference(), customizationOption.getPriceDifference());
            }
        }
    }

    @AfterAll
    void cleanUp() {
        server.stop();
    }
}
