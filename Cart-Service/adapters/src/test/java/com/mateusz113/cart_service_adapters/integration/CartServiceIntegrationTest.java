package com.mateusz113.cart_service_adapters.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import com.mateusz113.cart_service_adapters.mapper.CartMapper;
import com.mateusz113.cart_service_adapters.mapper.CustomizedProductMapper;
import com.mateusz113.cart_service_core.ports.outgoing.CartServiceDatabase;
import com.mateusz113.cart_service_model.cart.Cart;
import com.mateusz113.cart_service_model.customization.AppliedCustomization;
import com.mateusz113.cart_service_model.customization.AppliedCustomizationOption;
import com.mateusz113.cart_service_model.product.CustomizedProduct;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.mateusz113.cart_service_adapters.util.CartServiceAdaptersTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EnableWireMock(@ConfigureWireMock(name = "product-service-client", port = 8080))
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class CartServiceIntegrationTest {
    @InjectWireMock("product-service-client")
    private static WireMockServer wireMockServer;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private CustomizedProductMapper productMapper;
    @Autowired
    private CartServiceDatabase database;

    @Test
    void addCart_AddsCartToDatabaseAndReturnsCartDtoWithStatus201() throws Exception {
        stubClientMethod();

        ResultActions resultActions = mockMvc.perform(post("/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getInsertCartCommand())))
                .andDo(print())
                .andExpect(status().isCreated());
        checkResultActionsForCartDto(resultActions, true);
    }

    @Test
    void addProduct_AddsProductToCartAndReturnsStatus204() throws Exception {
        Long cartId = 1L;
        database.save(getCartWithoutIds());
        stubClientMethod();

        mockMvc.perform(patch("/carts/{cartId}/customizedProducts", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getInsertCustomizedProductCommand())))
                .andDo(print())
                .andExpect(status().isNoContent());

        //Looking for id 3 as 1 and 2 are taken by previous insert
        Optional<CustomizedProduct> result = database.findProductById(3L);
        result.ifPresentOrElse(
                product -> {
                    assertNotNull(product.getId());
                    assertEquals(1L, product.getSourceId());
                    assertEquals("source_name", product.getName());
                    assertEquals("source_brand", product.getBrand());
                    assertEquals(getDefaultPrice(), product.getPrice());
                    assertEquals(getDefaultQuantity(), product.getQuantity());
                    assertNull(product.getCart());
                    assertEquals(2, product.getAppliedCustomizations().size());
                    for (AppliedCustomization customization : product.getAppliedCustomizations()) {
                        assertNotNull(customization.getId());
                        assertEquals(1L, customization.getSourceId());
                        assertEquals("source_name", customization.getName());
                        assertEquals(true, customization.getMultipleChoice());
                        assertNull(customization.getProduct());
                        assertEquals(2, customization.getAppliedOptions().size());
                        for (AppliedCustomizationOption option : customization.getAppliedOptions()) {
                            assertNotNull(option.getId());
                            assertEquals(1L, option.getSourceId());
                            assertEquals("source_name", option.getName());
                            assertEquals(getDefaultPriceDifference(), option.getPriceDifference());
                            assertNull(option.getCustomization());
                        }
                    }
                },
                Assertions::fail
        );
    }

    @Test
    void getCart_ReturnsCartDtoWithStatus200() throws Exception {
        Long cartId = 1L;
        database.save(getCartWithoutIds());

        ResultActions resultActions = mockMvc.perform(get("/carts/{cartId}", cartId))
                .andDo(print())
                .andExpect(status().isOk());
        checkResultActionsForCartDto(resultActions, false);
    }

    @Test
    void deleteCart_DeletesCartAndReturnsStatus204() throws Exception {
        Long cartId = 1L;
        database.save(getCartWithoutIds());

        mockMvc.perform(delete("/carts/{cartId}", cartId))
                .andDo(print())
                .andExpect(status().isNoContent());

        Optional<Cart> result = database.findCartById(cartId);
        if (result.isPresent()) fail();
    }

    @Test
    void deleteProduct_DeletesProductAndReturnsStatus204() throws Exception {
        Long productId = 1L;
        database.save(getCartWithoutIds());

        mockMvc.perform(delete("/carts/customizedProducts/{productId}", productId))
                .andDo(print())
                .andExpect(status().isNoContent());

        Optional<CustomizedProduct> result = database.findProductById(productId);
        if (result.isPresent()) fail();
    }

    @AfterAll
    void cleanUp() {
        wireMockServer.stop();
    }

    private void stubClientMethod() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(getSourceProduct());
        UrlPattern urlTemplate = urlPathTemplate("/products/{productId}");
        wireMockServer.stubFor(WireMock.get(urlTemplate)
                .withPathParam("productId", equalTo(String.valueOf(1L)))
                .willReturn(okJson(response)));
    }

    private void checkResultActionsForCartDto(ResultActions resultActions, boolean usedSourceInfo) throws Exception {
        String name = usedSourceInfo ? "source_name" : "name";
        String brand = usedSourceInfo ? "source_brand" : "brand";
        resultActions.andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.customizedProducts").isNotEmpty())
                .andExpect(jsonPath("$.customizedProducts[0].id").value(1L))
                .andExpect(jsonPath("$.customizedProducts[0].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[0].name").value(name))
                .andExpect(jsonPath("$.customizedProducts[0].brand").value(brand))
                .andExpect(jsonPath("$.customizedProducts[0].price").value(getDefaultPrice()))
                .andExpect(jsonPath("$.customizedProducts[0].quantity").value(getDefaultQuantity()))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations").isNotEmpty())
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].id").value(1L))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].name").value(name))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].multipleChoice").value(true))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].appliedOptions").isNotEmpty())
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].appliedOptions[0].id").value(1L))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].appliedOptions[0].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].appliedOptions[0].name").value(name))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].appliedOptions[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].appliedOptions[1].id").value(2L))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].appliedOptions[1].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].appliedOptions[1].name").value(name))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].appliedOptions[1].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].id").value(2L))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].name").value(name))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].multipleChoice").value(true))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].appliedOptions").isNotEmpty())
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].appliedOptions[0].id").value(3L))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].appliedOptions[0].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].appliedOptions[0].name").value(name))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].appliedOptions[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].appliedOptions[1].id").value(4L))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].appliedOptions[1].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].appliedOptions[1].name").value(name))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].appliedOptions[1].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.customizedProducts[1].id").value(2L))
                .andExpect(jsonPath("$.customizedProducts[1].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[1].name").value(name))
                .andExpect(jsonPath("$.customizedProducts[1].brand").value(brand))
                .andExpect(jsonPath("$.customizedProducts[1].price").value(getDefaultPrice()))
                .andExpect(jsonPath("$.customizedProducts[1].quantity").value(getDefaultQuantity()))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations").isNotEmpty())
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].id").value(3L))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].name").value(name))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].multipleChoice").value(true))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].appliedOptions").isNotEmpty())
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].appliedOptions[0].id").value(5L))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].appliedOptions[0].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].appliedOptions[0].name").value(name))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].appliedOptions[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].appliedOptions[1].id").value(6L))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].appliedOptions[1].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].appliedOptions[1].name").value(name))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].appliedOptions[1].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].id").value(4L))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].name").value(name))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].multipleChoice").value(true))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].appliedOptions").isNotEmpty())
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].appliedOptions[0].id").value(7L))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].appliedOptions[0].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].appliedOptions[0].name").value(name))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].appliedOptions[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].appliedOptions[1].id").value(8L))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].appliedOptions[1].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].appliedOptions[1].name").value(name))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].appliedOptions[1].priceDifference").value(getDefaultPriceDifference()));
    }
}
