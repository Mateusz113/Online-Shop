package com.mateusz113.order_service_adapters.exception_handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderServiceExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void handleWebException_ReturnsCorrectResponse() throws Exception {
        String message = "Error message";

        mockMvc.perform(get("/exception/web_exception")
                        .param("message", message))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.statusCode").value(400));
    }
}