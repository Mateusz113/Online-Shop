package com.mateusz113.order_service_adapters.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mateusz113.order_service_adapters.mapper.OrderMapper;
import com.mateusz113.order_service_core.port.incoming.OrderServiceActionPorts;
import com.mateusz113.order_service_model.order.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getDefaultQuantity;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getDefaultTimeString;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getInsertOrderCommand;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getOrder;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getPageableContent;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderServiceControllerTest {
    @MockitoBean
    private OrderServiceActionPorts actionPorts;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Test
    void planeNewOrder_ReturnsOrderDtoWithStatus201() throws Exception {
        String payload = objectMapper.writeValueAsString(getInsertOrderCommand());
        Order mappedCommand = orderMapper.commandToModel(getInsertOrderCommand());
        when(actionPorts.place(mappedCommand)).thenReturn(getOrder());

        ResultActions resultActions = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andDo(print())
                .andExpect(status().isCreated());

        validateOrderDto("", resultActions);
    }

    @Test
    void getOrderById_ReturnsOrderDtoWithStatus200() throws Exception {
        Long orderId = 1L;
        when(actionPorts.getById(orderId)).thenReturn(getOrder());

        ResultActions resultActions = mockMvc.perform(get("/orders/{orderId}", orderId))
                .andDo(print())
                .andExpect(status().isOk());

        validateOrderDto("", resultActions);
    }

    @Test
    void getClientOrders_ReturnsPageableContentDtoWithStatus200() throws Exception {
        Pageable pageable = PageRequest.of(0, 2);
        Long clientId = 1L;
        when(actionPorts.getClientOrders(clientId, pageable.getPageNumber(), pageable.getPageSize())).thenReturn(getPageableContent());

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
        when(actionPorts.getAllOrders(pageable.getPageNumber(), pageable.getPageSize())).thenReturn(getPageableContent());

        ResultActions resultActions = mockMvc.perform(get("/orders")
                        .queryParam("page", String.valueOf(pageable.getPageNumber()))
                        .queryParam("size", String.valueOf(pageable.getPageSize())))
                .andDo(print())
                .andExpect(status().isOk());

        validatePageableContentDtoOfOrderDto(resultActions, pageable);
    }

    @Test
    void getOrderInvoice_ReturnsInvoiceWithStatus200() throws Exception {
        Long orderId = 1L;
        byte[] invoice = new byte[2];
        when(actionPorts.getOrderInvoice(orderId)).thenReturn(invoice);

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

        verify(actionPorts, times(1)).delete(orderId);
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
            validateOrderDto(prefix, resultActions);
        }
    }

    private void validateOrderDto(String pathPrefix, ResultActions resultActions) throws Exception {
        resultActions.andExpect(jsonPath("$%s.id".formatted(pathPrefix)).value(1L))
                .andExpect(jsonPath("$%s.cartId".formatted(pathPrefix)).value(1L))
                .andExpect(jsonPath("$%s.clientId".formatted(pathPrefix)).value(1L))
                .andExpect(jsonPath("$%s.clientComment".formatted(pathPrefix)).value("clientComment"))
                .andExpect(jsonPath("$%s.placementTime".formatted(pathPrefix)).value(getDefaultTimeString()))
                .andExpect(jsonPath("$%s.orderStatus".formatted(pathPrefix)).value("PENDING"))
                // 1st product
                .andExpect(jsonPath("$%s.products[0].id".formatted(pathPrefix)).value(1L))
                .andExpect(jsonPath("$%s.products[0].sourceId".formatted(pathPrefix)).value(1L))
                .andExpect(jsonPath("$%s.products[0].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[0].brand".formatted(pathPrefix)).value("brand"))
                .andExpect(jsonPath("$%s.products[0].price".formatted(pathPrefix)).value("10.0"))
                .andExpect(jsonPath("$%s.products[0].quantity".formatted(pathPrefix)).value(getDefaultQuantity()))
                // 1st product, 1st customization
                .andExpect(jsonPath("$%s.products[0].customizations[0].id".formatted(pathPrefix)).value(1L))
                .andExpect(jsonPath("$%s.products[0].customizations[0].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[0].customizations[0].multipleChoice".formatted(pathPrefix)).value(true))
                .andExpect(jsonPath("$%s.products[0].customizations[0].options[0].id".formatted(pathPrefix)).value(1L))
                .andExpect(jsonPath("$%s.products[0].customizations[0].options[0].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[0].customizations[0].options[0].priceDifference".formatted(pathPrefix)).value("1.0"))
                .andExpect(jsonPath("$%s.products[0].customizations[0].options[1].id".formatted(pathPrefix)).value(1L))
                .andExpect(jsonPath("$%s.products[0].customizations[0].options[1].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[0].customizations[0].options[1].priceDifference".formatted(pathPrefix)).value("1.0"))
                // 1st product, 2nd customization
                .andExpect(jsonPath("$%s.products[0].customizations[1].id".formatted(pathPrefix)).value(1L))
                .andExpect(jsonPath("$%s.products[0].customizations[1].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[0].customizations[1].multipleChoice".formatted(pathPrefix)).value(true))
                .andExpect(jsonPath("$%s.products[0].customizations[1].options[0].id".formatted(pathPrefix)).value(1L))
                .andExpect(jsonPath("$%s.products[0].customizations[1].options[0].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[0].customizations[1].options[0].priceDifference".formatted(pathPrefix)).value("1.0"))
                .andExpect(jsonPath("$%s.products[0].customizations[1].options[1].id".formatted(pathPrefix)).value(1L))
                .andExpect(jsonPath("$%s.products[0].customizations[1].options[1].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[0].customizations[1].options[1].priceDifference".formatted(pathPrefix)).value("1.0"))
                // 2nd product
                .andExpect(jsonPath("$%s.products[1].id".formatted(pathPrefix)).value(1L))
                .andExpect(jsonPath("$%s.products[1].sourceId".formatted(pathPrefix)).value(1L))
                .andExpect(jsonPath("$%s.products[1].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[1].brand".formatted(pathPrefix)).value("brand"))
                .andExpect(jsonPath("$%s.products[1].price".formatted(pathPrefix)).value("10.0"))
                .andExpect(jsonPath("$%s.products[1].quantity".formatted(pathPrefix)).value(getDefaultQuantity()))
                // 2nd product, 1st customization
                .andExpect(jsonPath("$%s.products[1].customizations[0].id".formatted(pathPrefix)).value(1L))
                .andExpect(jsonPath("$%s.products[1].customizations[0].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[1].customizations[0].multipleChoice".formatted(pathPrefix)).value(true))
                .andExpect(jsonPath("$%s.products[1].customizations[0].options[0].id".formatted(pathPrefix)).value(1L))
                .andExpect(jsonPath("$%s.products[1].customizations[0].options[0].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[1].customizations[0].options[0].priceDifference".formatted(pathPrefix)).value("1.0"))
                .andExpect(jsonPath("$%s.products[1].customizations[0].options[1].id".formatted(pathPrefix)).value(1L))
                .andExpect(jsonPath("$%s.products[1].customizations[0].options[1].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[1].customizations[0].options[1].priceDifference".formatted(pathPrefix)).value("1.0"))
                // 2nd product, 2nd customization
                .andExpect(jsonPath("$%s.products[1].customizations[1].id".formatted(pathPrefix)).value(1L))
                .andExpect(jsonPath("$%s.products[1].customizations[1].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[1].customizations[1].multipleChoice".formatted(pathPrefix)).value(true))
                .andExpect(jsonPath("$%s.products[1].customizations[1].options[0].id".formatted(pathPrefix)).value(1L))
                .andExpect(jsonPath("$%s.products[1].customizations[1].options[0].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[1].customizations[1].options[0].priceDifference".formatted(pathPrefix)).value("1.0"))
                .andExpect(jsonPath("$%s.products[1].customizations[1].options[1].id".formatted(pathPrefix)).value(1L))
                .andExpect(jsonPath("$%s.products[1].customizations[1].options[1].name".formatted(pathPrefix)).value("name"))
                .andExpect(jsonPath("$%s.products[1].customizations[1].options[1].priceDifference".formatted(pathPrefix)).value("1.0"));
    }
}
