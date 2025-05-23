package com.mateusz113.order_service_adapters.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import com.mateusz113.order_service_model.exception.CartNotFoundException;
import com.mateusz113.order_service_model.exception.ServiceCommunicationErrorException;
import com.mateusz113.order_service_model_public.dto.CartCustomizationDto;
import com.mateusz113.order_service_model_public.dto.CartCustomizationOptionDto;
import com.mateusz113.order_service_model_public.dto.CartDto;
import com.mateusz113.order_service_model_public.dto.CartProductDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.mateusz113.order_service_adapters.util.CartServiceAdaptersUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@EnableWireMock(@ConfigureWireMock(name = "cart-service-client", port = 8081))
public class CartServiceClientTest {
    @InjectWireMock("cart-service-client")
    private WireMockServer mockServer;
    @Autowired
    private CartServiceClient client;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getCartById_ClientServiceReturnsStatus404_ThrowCartNotFoundException() {
        Long cartId = 1L;
        UrlPattern template = urlPathTemplate("/carts/{cartId}");
        mockServer.stubFor(get(template)
                .withPathParam("cartId", equalTo(String.valueOf(cartId)))
                .willReturn(notFound()));

        CartNotFoundException exception = assertThrows(CartNotFoundException.class, () -> client.getCartById(cartId));

        assertEquals("Cart with id: 1 was not found.", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void getCartById_ClientServiceReturnsNotCaughtErrorStatus_ThrowServiceCommunicationErrorException() {
        Long cartId = 1L;
        UrlPattern template = urlPathTemplate("/carts/{cartId}");
        mockServer.stubFor(get(template)
                .withPathParam("cartId", equalTo(String.valueOf(cartId)))
                .willReturn(serverError()));

        ServiceCommunicationErrorException exception = assertThrows(ServiceCommunicationErrorException.class, () -> client.getCartById(cartId));

        assertEquals("Could not communicate with cart service.", exception.getMessage());
        assertEquals(500, exception.getStatusCode());
    }

    @Test
    void getCartById_ClientServiceReturnsCartDataWithStatus200_ReturnsCartDto() throws JsonProcessingException {
        Long cartId = 1L;
        String body = objectMapper.writeValueAsString(getCartDto());
        UrlPattern template = urlPathTemplate("/carts/{cartId}");
        mockServer.stubFor(get(template)
                .withPathParam("cartId", equalTo(String.valueOf(cartId)))
                .willReturn(okJson(body)));

        CartDto result = client.getCartById(cartId);

        assertEquals(2, result.customizedProducts().size());
        for (CartProductDto productDto : result.customizedProducts()) {
            assertEquals(1L, productDto.id());
            assertEquals(1L, productDto.sourceId());
            assertEquals("name", productDto.name());
            assertEquals("brand", productDto.brand());
            assertEquals(getDefaultPrice(), productDto.price());
            assertEquals(getDefaultQuantity(), productDto.quantity());
            for (CartCustomizationDto customizationDto : productDto.appliedCustomizations()) {
                assertEquals("name", customizationDto.name());
                assertEquals(true, customizationDto.multipleChoice());
                for (CartCustomizationOptionDto customizationOptionDto : customizationDto.appliedOptions()) {
                    assertEquals("name", customizationOptionDto.name());
                    assertEquals(getDefaultPriceDifference(), customizationOptionDto.priceDifference());
                }
            }
        }
    }

    @Test
    void deleteCart_DeletesCart() {
        Long cartId = 1L;
        UrlPattern template = urlPathTemplate("/carts/{cartId}");
        mockServer.stubFor(delete(template)
                .withPathParam("cartId", equalTo(String.valueOf(cartId)))
                .willReturn(noContent()));

        client.deleteCart(cartId);

        verify(deleteRequestedFor(template).withPathParam("cartId", equalTo(String.valueOf(cartId))));
    }
}
