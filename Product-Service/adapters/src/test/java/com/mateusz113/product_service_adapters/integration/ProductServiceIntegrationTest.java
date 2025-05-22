package com.mateusz113.product_service_adapters.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mateusz113.product_service_core.ports.outgoing.ProductServiceDatabase;
import com.mateusz113.product_service_model.customization.CustomizationOption;
import com.mateusz113.product_service_model.product.Product;
import com.mateusz113.product_service_model_public.commands.UpsertCustomizationOptionCommand;
import com.mateusz113.product_service_model_public.commands.UpsertProductCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.mateusz113.product_service_adapters.util.ProductServiceAdaptersTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class ProductServiceIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductServiceDatabase database;

    @BeforeEach
    void setUp() {
        IntStream.of(0, 1).forEach(i -> database.save(getProductWithoutIds()));
    }

    @Test
    void addNewProducts_SavesProductsAndReturnsListOfProductDtoWithStatus201() throws Exception {
        List<UpsertProductCommand> commands = List.of(getUpsertProductCommand(), getUpsertProductCommand());

        mockMvc.perform(post("/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commands)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].brand").value("brand"))
                .andExpect(jsonPath("$[0].price").value(getDefaultPrice()))
                .andExpect(jsonPath("$[0].type").value("type"))
                .andExpect(jsonPath("$[0].availableAmount").value(getDefaultAvailableAmount()))
                .andExpect(jsonPath("$[0].customizations[0].id").value(5))
                .andExpect(jsonPath("$[0].customizations[0].name").value("name"))
                .andExpect(jsonPath("$[0].customizations[0].multipleChoice").value(true))
                .andExpect(jsonPath("$[0].customizations[0].options[0].id").value(9))
                .andExpect(jsonPath("$[0].customizations[0].options[0].name").value("name"))
                .andExpect(jsonPath("$[0].customizations[0].options[0].defaultOption").value(false))
                .andExpect(jsonPath("$[0].customizations[0].options[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$[0].customizations[0].options[1].id").value(10))
                .andExpect(jsonPath("$[0].customizations[0].options[1].name").value("name"))
                .andExpect(jsonPath("$[0].customizations[0].options[1].defaultOption").value(false))
                .andExpect(jsonPath("$[0].customizations[0].options[1].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$[0].customizations[1].id").value(6))
                .andExpect(jsonPath("$[0].customizations[1].name").value("name"))
                .andExpect(jsonPath("$[0].customizations[1].multipleChoice").value(true))
                .andExpect(jsonPath("$[0].customizations[1].options[0].id").value(11))
                .andExpect(jsonPath("$[0].customizations[1].options[0].name").value("name"))
                .andExpect(jsonPath("$[0].customizations[1].options[0].defaultOption").value(false))
                .andExpect(jsonPath("$[0].customizations[1].options[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$[0].customizations[1].options[1].id").value(12))
                .andExpect(jsonPath("$[0].customizations[1].options[1].name").value("name"))
                .andExpect(jsonPath("$[0].customizations[1].options[1].defaultOption").value(false))
                .andExpect(jsonPath("$[0].customizations[1].options[1].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$[1].id").value(4))
                .andExpect(jsonPath("$[1].name").value("name"))
                .andExpect(jsonPath("$[1].brand").value("brand"))
                .andExpect(jsonPath("$[1].price").value(getDefaultPrice()))
                .andExpect(jsonPath("$[1].type").value("type"))
                .andExpect(jsonPath("$[1].availableAmount").value(getDefaultAvailableAmount()))
                .andExpect(jsonPath("$[1].customizations[0].id").value(7))
                .andExpect(jsonPath("$[1].customizations[0].name").value("name"))
                .andExpect(jsonPath("$[1].customizations[0].multipleChoice").value(true))
                .andExpect(jsonPath("$[1].customizations[0].options[0].id").value(13))
                .andExpect(jsonPath("$[1].customizations[0].options[0].name").value("name"))
                .andExpect(jsonPath("$[1].customizations[0].options[0].defaultOption").value(false))
                .andExpect(jsonPath("$[1].customizations[0].options[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$[1].customizations[0].options[1].id").value(14))
                .andExpect(jsonPath("$[1].customizations[0].options[1].name").value("name"))
                .andExpect(jsonPath("$[1].customizations[0].options[1].defaultOption").value(false))
                .andExpect(jsonPath("$[1].customizations[0].options[1].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$[1].customizations[1].id").value(8))
                .andExpect(jsonPath("$[1].customizations[1].name").value("name"))
                .andExpect(jsonPath("$[1].customizations[1].multipleChoice").value(true))
                .andExpect(jsonPath("$[1].customizations[1].options[0].id").value(15))
                .andExpect(jsonPath("$[1].customizations[1].options[0].name").value("name"))
                .andExpect(jsonPath("$[1].customizations[1].options[0].defaultOption").value(false))
                .andExpect(jsonPath("$[1].customizations[1].options[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$[1].customizations[1].options[1].id").value(16))
                .andExpect(jsonPath("$[1].customizations[1].options[1].name").value("name"))
                .andExpect(jsonPath("$[1].customizations[1].options[1].defaultOption").value(false))
                .andExpect(jsonPath("$[1].customizations[1].options[1].priceDifference").value(getDefaultPriceDifference()));
    }

    @Test
    void getProducts_ReturnsProductDtoPageableContentDtoWithStatus200() throws Exception {
        int pageNumber = 0;
        int pageSize = 2;

        mockMvc.perform(get("/products")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.pageNumber").value(pageNumber))
                .andExpect(jsonPath("$.pageSize").value(pageSize))
                .andExpect(jsonPath("$.elements").isNotEmpty())
                .andExpect(jsonPath("$.elements[0].id").value(1))
                .andExpect(jsonPath("$.elements[0].name").value("name"))
                .andExpect(jsonPath("$.elements[0].brand").value("brand"))
                .andExpect(jsonPath("$.elements[0].price").value(getDefaultPrice()))
                .andExpect(jsonPath("$.elements[0].type").value("type"))
                .andExpect(jsonPath("$.elements[0].availableAmount").value(getDefaultAvailableAmount()))
                .andExpect(jsonPath("$.elements[0].customizations[0].id").value(1))
                .andExpect(jsonPath("$.elements[0].customizations[0].name").value("name"))
                .andExpect(jsonPath("$.elements[0].customizations[0].multipleChoice").value(true))
                .andExpect(jsonPath("$.elements[0].customizations[0].options[0].id").value(1))
                .andExpect(jsonPath("$.elements[0].customizations[0].options[0].name").value("name"))
                .andExpect(jsonPath("$.elements[0].customizations[0].options[0].defaultOption").value(false))
                .andExpect(jsonPath("$.elements[0].customizations[0].options[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.elements[0].customizations[0].options[1].id").value(2))
                .andExpect(jsonPath("$.elements[0].customizations[0].options[1].name").value("name"))
                .andExpect(jsonPath("$.elements[0].customizations[0].options[1].defaultOption").value(false))
                .andExpect(jsonPath("$.elements[0].customizations[0].options[1].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.elements[0].customizations[1].id").value(2))
                .andExpect(jsonPath("$.elements[0].customizations[1].name").value("name"))
                .andExpect(jsonPath("$.elements[0].customizations[1].multipleChoice").value(true))
                .andExpect(jsonPath("$.elements[0].customizations[1].options[0].id").value(3))
                .andExpect(jsonPath("$.elements[0].customizations[1].options[0].name").value("name"))
                .andExpect(jsonPath("$.elements[0].customizations[1].options[0].defaultOption").value(false))
                .andExpect(jsonPath("$.elements[0].customizations[1].options[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.elements[0].customizations[1].options[1].id").value(4))
                .andExpect(jsonPath("$.elements[0].customizations[1].options[1].name").value("name"))
                .andExpect(jsonPath("$.elements[0].customizations[1].options[1].defaultOption").value(false))
                .andExpect(jsonPath("$.elements[0].customizations[1].options[1].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.elements[1].id").value(2))
                .andExpect(jsonPath("$.elements[1].name").value("name"))
                .andExpect(jsonPath("$.elements[1].brand").value("brand"))
                .andExpect(jsonPath("$.elements[1].price").value(getDefaultPrice()))
                .andExpect(jsonPath("$.elements[1].type").value("type"))
                .andExpect(jsonPath("$.elements[1].availableAmount").value(getDefaultAvailableAmount()))
                .andExpect(jsonPath("$.elements[1].customizations[0].id").value(3))
                .andExpect(jsonPath("$.elements[1].customizations[0].name").value("name"))
                .andExpect(jsonPath("$.elements[1].customizations[0].multipleChoice").value(true))
                .andExpect(jsonPath("$.elements[1].customizations[0].options[0].id").value(5))
                .andExpect(jsonPath("$.elements[1].customizations[0].options[0].name").value("name"))
                .andExpect(jsonPath("$.elements[1].customizations[0].options[0].defaultOption").value(false))
                .andExpect(jsonPath("$.elements[1].customizations[0].options[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.elements[1].customizations[0].options[1].id").value(6))
                .andExpect(jsonPath("$.elements[1].customizations[0].options[1].name").value("name"))
                .andExpect(jsonPath("$.elements[1].customizations[0].options[1].defaultOption").value(false))
                .andExpect(jsonPath("$.elements[1].customizations[0].options[1].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.elements[1].customizations[1].id").value(4))
                .andExpect(jsonPath("$.elements[1].customizations[1].name").value("name"))
                .andExpect(jsonPath("$.elements[1].customizations[1].multipleChoice").value(true))
                .andExpect(jsonPath("$.elements[1].customizations[1].options[0].id").value(7))
                .andExpect(jsonPath("$.elements[1].customizations[1].options[0].name").value("name"))
                .andExpect(jsonPath("$.elements[1].customizations[1].options[0].defaultOption").value(false))
                .andExpect(jsonPath("$.elements[1].customizations[1].options[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.elements[1].customizations[1].options[1].id").value(8))
                .andExpect(jsonPath("$.elements[1].customizations[1].options[1].name").value("name"))
                .andExpect(jsonPath("$.elements[1].customizations[1].options[1].defaultOption").value(false))
                .andExpect(jsonPath("$.elements[1].customizations[1].options[1].priceDifference").value(getDefaultPriceDifference()));
    }

    @Test
    void getProductById_ReturnsDetailedProductDtoWithStatus200() throws Exception {
        Long id = 1L;

        mockMvc.perform(get("/products/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.brand").value("brand"))
                .andExpect(jsonPath("$.price").value(getDefaultPrice()))
                .andExpect(jsonPath("$.type").value("type"))
                .andExpect(jsonPath("$.availableAmount").value(getDefaultAvailableAmount()))
                .andExpect(jsonPath("$.details[0].id").value(1))
                .andExpect(jsonPath("$.details[0].label").value("label"))
                .andExpect(jsonPath("$.details[0].description").value("description"))
                .andExpect(jsonPath("$.details[1].id").value(2))
                .andExpect(jsonPath("$.details[1].label").value("label"))
                .andExpect(jsonPath("$.details[1].description").value("description"))
                .andExpect(jsonPath("$.customizations[0].id").value(1))
                .andExpect(jsonPath("$.customizations[0].name").value("name"))
                .andExpect(jsonPath("$.customizations[0].multipleChoice").value(true))
                .andExpect(jsonPath("$.customizations[0].options[0].id").value(1))
                .andExpect(jsonPath("$.customizations[0].options[0].name").value("name"))
                .andExpect(jsonPath("$.customizations[0].options[0].defaultOption").value(false))
                .andExpect(jsonPath("$.customizations[0].options[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.customizations[0].options[1].id").value(2))
                .andExpect(jsonPath("$.customizations[0].options[1].name").value("name"))
                .andExpect(jsonPath("$.customizations[0].options[1].defaultOption").value(false))
                .andExpect(jsonPath("$.customizations[0].options[1].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.customizations[1].id").value(2))
                .andExpect(jsonPath("$.customizations[1].name").value("name"))
                .andExpect(jsonPath("$.customizations[1].multipleChoice").value(true))
                .andExpect(jsonPath("$.customizations[1].options[0].id").value(3))
                .andExpect(jsonPath("$.customizations[1].options[0].name").value("name"))
                .andExpect(jsonPath("$.customizations[1].options[0].defaultOption").value(false))
                .andExpect(jsonPath("$.customizations[1].options[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.customizations[1].options[1].id").value(4))
                .andExpect(jsonPath("$.customizations[1].options[1].name").value("name"))
                .andExpect(jsonPath("$.customizations[1].options[1].defaultOption").value(false))
                .andExpect(jsonPath("$.customizations[1].options[1].priceDifference").value(getDefaultPriceDifference()));
    }

    @Test
    void checkProductsAvailableAmounts_ChecksIfTheRequiredAmountIsAvailableAndReturnsStatus204() throws Exception {
        Map<Long, Integer> productsStockMap = Map.of(1L, 5, 2L, 5);

        mockMvc.perform(get("/products/available-amount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productsStockMap)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void updateProductsAvailableAmounts_UpdatesProductsAvailableAmountsAndReturnsStatus204() throws Exception {
        Map<Long, Integer> productsStockMap = Map.of(1L, 5, 2L, 5);

        mockMvc.perform(patch("/products/available-amount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productsStockMap)))
                .andDo(print())
                .andExpect(status().isNoContent());

        List<Long> ids = productsStockMap.keySet().stream().toList();
        List<Product> products = database.findAllByIds(ids);
        products.forEach(product -> assertEquals(5, product.getAvailableAmount()));
    }

    @Test
    void updateProduct_UpdatesProductAndReturnsStatus204() throws Exception {
        Long id = 1L;
        UpsertProductCommand upsertProductCommand = getUpsertProductCommand();

        mockMvc.perform(patch("/products/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(upsertProductCommand)))
                .andDo(print())
                .andExpect(status().isNoContent());
        Product product = database.findById(id).orElseThrow(IllegalStateException::new);
        assertEquals(upsertProductCommand.name(), product.getName());
        assertEquals(upsertProductCommand.brand(), product.getBrand());
        assertEquals(upsertProductCommand.price(), product.getPrice());
        assertEquals(upsertProductCommand.type(), product.getType());
        assertEquals(upsertProductCommand.availableAmount(), product.getAvailableAmount());
        for (int i = 0; i < 2; i++) {
            assertEquals(upsertProductCommand.details().get(i).label(), product.getDetails().get(i).getLabel());
            assertEquals(upsertProductCommand.details().get(i).description(), product.getDetails().get(i).getDescription());
            assertEquals(upsertProductCommand.customizations().get(i).name(), product.getCustomizations().get(i).getName());
            assertEquals(upsertProductCommand.customizations().get(i).multipleChoice(), product.getCustomizations().get(i).getMultipleChoice());
            for (int j = 0; j < 2; j++) {
                List<UpsertCustomizationOptionCommand> customizationOptionCommands = upsertProductCommand.customizations().get(i).options();
                List<CustomizationOption> customizationOptions = product.getCustomizations().get(i).getOptions();
                assertEquals(customizationOptionCommands.get(j).name(), customizationOptions.get(j).getName());
                assertEquals(customizationOptionCommands.get(j).defaultOption(), customizationOptions.get(j).getDefaultOption());
                assertEquals(customizationOptionCommands.get(j).priceDifference(), customizationOptions.get(j).getPriceDifference());
            }
        }
    }

    @Test
    void deleteProduct_DeletesProductAndReturnsStatus204() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/products/{id}", id))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertTrue(database.findById(id).isEmpty());
    }
}
