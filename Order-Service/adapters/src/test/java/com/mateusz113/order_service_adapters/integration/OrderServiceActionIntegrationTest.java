package com.mateusz113.order_service_adapters.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.mateusz113.order_service_adapters.repository.OrderEntityRepository;
import com.mateusz113.order_service_core.port.outgoing.OrderServiceDatabase;
import com.mateusz113.order_service_model.order.Order;
import com.mateusz113.order_service_model.order.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathTemplate;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getCartDto;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getDefaultQuantity;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getInsertOrderCommand;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getOrderWithoutIds;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getOrderWithoutIdsWithInvoice;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@EnableWireMock(value = {
        @ConfigureWireMock(name = "product-service-client", port = 8080),
        @ConfigureWireMock(name = "cart-service-client", port = 8081)
})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderServiceActionIntegrationTest {
    @InjectWireMock("product-service-client")
    private WireMockServer productServer;
    @InjectWireMock("cart-service-client")
    private WireMockServer cartServer;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrderServiceDatabase database;
    @Autowired
    private OrderEntityRepository repository;
    private long ordersInDatabase;

    @BeforeEach
    void setUp() {
        ordersInDatabase = 2;
        for (int i = 0; i < ordersInDatabase; i++) {
            database.save(getOrderWithoutIds());
        }
    }

    @Test
    void placeNewOrder_CreatesNewOrderAndReturnsOrderDto() throws Exception {
        // TODO: To napraw
        String payload = objectMapper.writeValueAsString(getInsertOrderCommand());

        cartServer.stubFor(WireMock.get(urlPathTemplate("/carts/{cartId}"))
                .withPathParam("cartId", equalTo(String.valueOf(1L)))
                .willReturn(okJson(objectMapper.writeValueAsString(getCartDto())))
        );
        productServer.stubFor(WireMock.get(urlPathTemplate("/products/{productId}/available-amount"))
                .withPathParam("productId", equalTo(String.valueOf(1)))
                .withQueryParam("requiredStock", equalTo(String.valueOf(20)))
                .willReturn(ok())
        );

        ResultActions resultActions = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andDo(print())
                .andExpect(status().isCreated());

        validateOrderDto("", resultActions, null);
    }

    @Test
    void getOrderById_ReturnsOrderDtoWithStatus200() throws Exception {
        Long orderId = 1L;

        ResultActions resultActions = mockMvc.perform(get("/orders/{orderId}", orderId))
                .andDo(print())
                .andExpect(status().isOk());

        validateOrderDto("", resultActions, 1L);
    }

    @Test
    void getClientOrders_ReturnsPageableContentDtoWithStatus200() throws Exception {
        Pageable pageable = PageRequest.of(0, 2);
        Long clientId = 1L;

        ResultActions resultActions = mockMvc.perform(get("/orders/client-id/{clientId}", clientId)
                        .queryParam("page", String.valueOf(pageable.getPageNumber()))
                        .queryParam("size", String.valueOf(pageable.getPageSize())))
                .andDo(print())
                .andExpect(status().isOk());

        validatePageableContentDtoOfOrderDto(resultActions, pageable);
    }

    @Test
    void getAllOrders_ReturnsPageableContentDtoWithStatus200() throws Exception {
        Pageable pageable = PageRequest.of(0, 2);

        ResultActions resultActions = mockMvc.perform(get("/orders")
                        .queryParam("page", String.valueOf(pageable.getPageNumber()))
                        .queryParam("size", String.valueOf(pageable.getPageSize())))
                .andDo(print())
                .andExpect(status().isOk());

        validatePageableContentDtoOfOrderDto(resultActions, pageable);
    }

    @Test
    void getOrderInvoice_ReturnsInvoiceWithStatus200() throws Exception {
        Long orderId = 3L;
        byte[] invoice = new byte[2];
        Order order = getOrderWithoutIdsWithInvoice(invoice);
        order.setOrderStatus(OrderStatus.PAID);
        database.save(order);

        mockMvc.perform(get("/orders/{orderId}/invoice", orderId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().bytes(invoice));
    }

    @Test
    void deleteOrder_ReturnsOrderWithStatus204() throws Exception {
        Long orderId = 1L;

        mockMvc.perform(delete("/orders/{orderId}", orderId))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertTrue(repository.findById(orderId).isEmpty());
    }

    private void validatePageableContentDtoOfOrderDto(ResultActions resultActions, Pageable pageable) throws Exception {
        resultActions
                .andExpect(jsonPath("$.totalPages").value(Math.ceil((double) 2 / pageable.getPageSize())))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.pageNumber").value(pageable.getPageNumber()))
                .andExpect(jsonPath("$.pageSize").value(pageable.getPageSize()));

        validateOrderDtoList("elements", resultActions);
    }

    private void validateOrderDtoList(String arrayName, ResultActions resultActions) throws Exception {
        for (int i = 0; i < 2; i++) {
            String prefix = ".%s[%d]".formatted(arrayName, i);
            validateOrderDto(prefix, resultActions, (long) i + 1);
        }
    }

    private void validateOrderDto(String pathPrefix, ResultActions resultActions, Long idOffset) throws Exception {
        if (Objects.isNull(idOffset)) {
            idOffset = ordersInDatabase + 1;
        }

        resultActions.andExpect(jsonPath("$%s.id".formatted(pathPrefix)).value(idOffset))
                .andExpect(jsonPath("$%s.cartId".formatted(pathPrefix)).value(1L))
                .andExpect(jsonPath("$%s.clientId".formatted(pathPrefix)).value(1L))
                .andExpect(jsonPath("$%s.clientComment".formatted(pathPrefix)).value("clientComment"))
                .andExpect(jsonPath("$%s.orderStatus".formatted(pathPrefix)).value("PENDING"))
                // 1st product
                .andExpect(jsonPath("$%s.products[0].id".formatted(pathPrefix)).value(idOffset * 2 - 1))
                .andExpect(jsonPath("$%s.products[0].sourceId".formatted(pathPrefix)).value(1L))
                .andExpect(jsonPath("$%s.products[0].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[0].brand".formatted(pathPrefix)).value("brand"))
                .andExpect(jsonPath("$%s.products[0].price".formatted(pathPrefix)).value("10.0"))
                .andExpect(jsonPath("$%s.products[0].quantity".formatted(pathPrefix)).value(getDefaultQuantity()))
                // 1st product, 1st customization
                .andExpect(jsonPath("$%s.products[0].customizations[0].id".formatted(pathPrefix)).value(idOffset * 4 - 3))
                .andExpect(jsonPath("$%s.products[0].customizations[0].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[0].customizations[0].multipleChoice".formatted(pathPrefix)).value(true))
                .andExpect(jsonPath("$%s.products[0].customizations[0].options[0].id".formatted(pathPrefix)).value(idOffset * 8 - 7))
                .andExpect(jsonPath("$%s.products[0].customizations[0].options[0].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[0].customizations[0].options[0].priceDifference".formatted(pathPrefix)).value("1.0"))
                .andExpect(jsonPath("$%s.products[0].customizations[0].options[1].id".formatted(pathPrefix)).value(idOffset * 8 - 6))
                .andExpect(jsonPath("$%s.products[0].customizations[0].options[1].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[0].customizations[0].options[1].priceDifference".formatted(pathPrefix)).value("1.0"))
                // 1st product, 2nd customization
                .andExpect(jsonPath("$%s.products[0].customizations[1].id".formatted(pathPrefix)).value(idOffset * 4 - 2))
                .andExpect(jsonPath("$%s.products[0].customizations[1].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[0].customizations[1].multipleChoice".formatted(pathPrefix)).value(true))
                .andExpect(jsonPath("$%s.products[0].customizations[1].options[0].id".formatted(pathPrefix)).value(idOffset * 8 - 5))
                .andExpect(jsonPath("$%s.products[0].customizations[1].options[0].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[0].customizations[1].options[0].priceDifference".formatted(pathPrefix)).value("1.0"))
                .andExpect(jsonPath("$%s.products[0].customizations[1].options[1].id".formatted(pathPrefix)).value(idOffset * 8 - 4))
                .andExpect(jsonPath("$%s.products[0].customizations[1].options[1].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[0].customizations[1].options[1].priceDifference".formatted(pathPrefix)).value("1.0"))
                // 2nd product
                .andExpect(jsonPath("$%s.products[1].id".formatted(pathPrefix)).value(idOffset * 2))
                .andExpect(jsonPath("$%s.products[1].sourceId".formatted(pathPrefix)).value(1L))
                .andExpect(jsonPath("$%s.products[1].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[1].brand".formatted(pathPrefix)).value("brand"))
                .andExpect(jsonPath("$%s.products[1].price".formatted(pathPrefix)).value("10.0"))
                .andExpect(jsonPath("$%s.products[1].quantity".formatted(pathPrefix)).value(getDefaultQuantity()))
                // 2nd product, 1st customization
                .andExpect(jsonPath("$%s.products[1].customizations[0].id".formatted(pathPrefix)).value(idOffset * 4 - 1))
                .andExpect(jsonPath("$%s.products[1].customizations[0].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[1].customizations[0].multipleChoice".formatted(pathPrefix)).value(true))
                .andExpect(jsonPath("$%s.products[1].customizations[0].options[0].id".formatted(pathPrefix)).value(idOffset * 8 - 3))
                .andExpect(jsonPath("$%s.products[1].customizations[0].options[0].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[1].customizations[0].options[0].priceDifference".formatted(pathPrefix)).value("1.0"))
                .andExpect(jsonPath("$%s.products[1].customizations[0].options[1].id".formatted(pathPrefix)).value(idOffset * 8 - 2))
                .andExpect(jsonPath("$%s.products[1].customizations[0].options[1].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[1].customizations[0].options[1].priceDifference".formatted(pathPrefix)).value("1.0"))
                // 2nd product, 2nd customization
                .andExpect(jsonPath("$%s.products[1].customizations[1].id".formatted(pathPrefix)).value(idOffset * 4))
                .andExpect(jsonPath("$%s.products[1].customizations[1].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[1].customizations[1].multipleChoice".formatted(pathPrefix)).value(true))
                .andExpect(jsonPath("$%s.products[1].customizations[1].options[0].id".formatted(pathPrefix)).value(idOffset * 8 - 1))
                .andExpect(jsonPath("$%s.products[1].customizations[1].options[0].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[1].customizations[1].options[0].priceDifference".formatted(pathPrefix)).value("1.0"))
                .andExpect(jsonPath("$%s.products[1].customizations[1].options[1].id".formatted(pathPrefix)).value(idOffset * 8))
                .andExpect(jsonPath("$%s.products[1].customizations[1].options[1].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[1].customizations[1].options[1].priceDifference".formatted(pathPrefix)).value("1.0"));
    }

    @AfterAll
    void cleanup() {
        productServer.stop();
        cartServer.stop();
    }
}
