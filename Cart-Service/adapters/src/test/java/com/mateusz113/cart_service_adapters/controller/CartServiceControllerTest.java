package com.mateusz113.cart_service_adapters.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mateusz113.cart_service_adapters.mapper.CartMapper;
import com.mateusz113.cart_service_adapters.mapper.CustomizedProductMapper;
import com.mateusz113.cart_service_core.ports.incoming.AddCart;
import com.mateusz113.cart_service_core.ports.incoming.AddProduct;
import com.mateusz113.cart_service_core.ports.incoming.DeleteCart;
import com.mateusz113.cart_service_core.ports.incoming.DeleteProduct;
import com.mateusz113.cart_service_core.ports.incoming.GetCart;
import com.mateusz113.cart_service_model.cart.Cart;
import com.mateusz113.cart_service_model.product.CustomizedProduct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.mateusz113.cart_service_adapters.util.CartServiceAdaptersTestUtil.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CartServiceControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private CustomizedProductMapper productMapper;
    @MockitoBean
    private AddCart addCart;
    @MockitoBean
    private AddProduct addProduct;
    @MockitoBean
    private GetCart getCart;
    @MockitoBean
    private DeleteCart deleteCart;
    @MockitoBean
    private DeleteProduct deleteProduct;

    @Test
    void addCart_ReturnsCartDtoWithStatus201() throws Exception {
        Cart mappedCart = cartMapper.commandToModel(getInsertCartCommand());
        when(addCart.add(mappedCart)).thenReturn(getCart());

        ResultActions resultActions = mockMvc.perform(post("/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getInsertCartCommand())))
                .andDo(print())
                .andExpect(status().isCreated());
        checkResultActionsForCartDto(resultActions);
    }

    @Test
    void addProduct_ReturnsStatus204() throws Exception {
        Long cartId = 1L;
        CustomizedProduct mappedProduct = productMapper.commandToModel(getInsertCustomizedProductCommand());

        mockMvc.perform(patch("/carts/{cartId}/customizedProducts", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getInsertCustomizedProductCommand())))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(addProduct, times(1)).add(mappedProduct, cartId);
    }

    @Test
    void getCart_ReturnsCartDtoWithStatus200() throws Exception {
        Long cartId = 1L;
        when(getCart.getCart(cartId)).thenReturn(getCart());

        ResultActions resultActions = mockMvc.perform(get("/carts/{cartId}", cartId))
                .andDo(print())
                .andExpect(status().isOk());
        checkResultActionsForCartDto(resultActions);
    }

    @Test
    void deleteCart_ReturnsStatus204() throws Exception {
        Long cartId = 1L;

        mockMvc.perform(delete("/carts/{cartId}", cartId))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(deleteCart, times(1)).deleteCart(cartId);
    }

    @Test
    void deleteProduct_ReturnsStatus204() throws Exception {
        Long productId = 1L;

        mockMvc.perform(delete("/carts/customizedProducts/{productId}", productId))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(deleteProduct, times(1)).deleteProduct(productId);
    }

    private void checkResultActionsForCartDto(ResultActions resultActions) throws Exception {
        resultActions.andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.creationDate").value(getDefaultTimeString()))
                .andExpect(jsonPath("$.customizedProducts").isNotEmpty())
                .andExpect(jsonPath("$.customizedProducts[0].id").value(1L))
                .andExpect(jsonPath("$.customizedProducts[0].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[0].name").value("name"))
                .andExpect(jsonPath("$.customizedProducts[0].brand").value("brand"))
                .andExpect(jsonPath("$.customizedProducts[0].price").value(getDefaultPrice()))
                .andExpect(jsonPath("$.customizedProducts[0].quantity").value(getDefaultQuantity()))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations").isNotEmpty())
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].id").value(1L))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].name").value("name"))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].multipleChoice").value(true))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].appliedOptions").isNotEmpty())
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].appliedOptions[0].id").value(1L))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].appliedOptions[0].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].appliedOptions[0].name").value("name"))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].appliedOptions[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].appliedOptions[1].id").value(1L))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].appliedOptions[1].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].appliedOptions[1].name").value("name"))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[0].appliedOptions[1].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].id").value(1L))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].name").value("name"))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].multipleChoice").value(true))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].appliedOptions").isNotEmpty())
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].appliedOptions[0].id").value(1L))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].appliedOptions[0].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].appliedOptions[0].name").value("name"))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].appliedOptions[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].appliedOptions[1].id").value(1L))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].appliedOptions[1].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].appliedOptions[1].name").value("name"))
                .andExpect(jsonPath("$.customizedProducts[0].appliedCustomizations[1].appliedOptions[1].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.customizedProducts[1].id").value(1L))
                .andExpect(jsonPath("$.customizedProducts[1].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[1].name").value("name"))
                .andExpect(jsonPath("$.customizedProducts[1].brand").value("brand"))
                .andExpect(jsonPath("$.customizedProducts[1].price").value(getDefaultPrice()))
                .andExpect(jsonPath("$.customizedProducts[1].quantity").value(getDefaultQuantity()))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations").isNotEmpty())
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].id").value(1L))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].name").value("name"))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].multipleChoice").value(true))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].appliedOptions").isNotEmpty())
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].appliedOptions[0].id").value(1L))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].appliedOptions[0].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].appliedOptions[0].name").value("name"))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].appliedOptions[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].appliedOptions[1].id").value(1L))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].appliedOptions[1].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].appliedOptions[1].name").value("name"))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[0].appliedOptions[1].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].id").value(1L))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].name").value("name"))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].multipleChoice").value(true))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].appliedOptions").isNotEmpty())
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].appliedOptions[0].id").value(1L))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].appliedOptions[0].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].appliedOptions[0].name").value("name"))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].appliedOptions[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].appliedOptions[1].id").value(1L))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].appliedOptions[1].sourceId").value(1L))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].appliedOptions[1].name").value("name"))
                .andExpect(jsonPath("$.customizedProducts[1].appliedCustomizations[1].appliedOptions[1].priceDifference").value(getDefaultPriceDifference()));
    }
}
