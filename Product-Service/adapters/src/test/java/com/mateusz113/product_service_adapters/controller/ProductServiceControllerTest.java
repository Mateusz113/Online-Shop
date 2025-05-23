package com.mateusz113.product_service_adapters.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mateusz113.product_service_adapters.mapper.ProductMapper;
import com.mateusz113.product_service_core.ports.incoming.AddNewProducts;
import com.mateusz113.product_service_core.ports.incoming.CheckProductsStock;
import com.mateusz113.product_service_core.ports.incoming.DeleteProduct;
import com.mateusz113.product_service_core.ports.incoming.GetDetailedProduct;
import com.mateusz113.product_service_core.ports.incoming.GetProducts;
import com.mateusz113.product_service_core.ports.incoming.UpdateProduct;
import com.mateusz113.product_service_core.ports.incoming.UpdateProductsStock;
import com.mateusz113.product_service_model.content_management.PageableContent;
import com.mateusz113.product_service_model.filter.ProductFilter;
import com.mateusz113.product_service_model.product.Product;
import com.mateusz113.product_service_model_public.commands.UpsertProductCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static com.mateusz113.product_service_adapters.util.ProductServiceAdaptersTestUtil.*;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductServiceControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductMapper productMapper;
    @MockitoBean
    public AddNewProducts addNewProducts;
    @MockitoBean
    public DeleteProduct deleteProduct;
    @MockitoBean
    public GetDetailedProduct getDetailedProduct;
    @MockitoBean
    public GetProducts getProducts;
    @MockitoBean
    public CheckProductsStock checkProductsStock;
    @MockitoBean
    public UpdateProductsStock updateProductsStock;
    @MockitoBean
    public UpdateProduct updateProduct;

    @Test
    void addNewProducts_ReturnsProductDtoListWithStatus201() throws Exception {
        List<UpsertProductCommand> upsertProductCommands = List.of(getUpsertProductCommand(), getUpsertProductCommand());
        List<Product> products = productMapper.commandListToModelList(upsertProductCommands);
        when(addNewProducts.add(products)).thenReturn(products);
        mockMvc.perform(
                        post("/products")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(upsertProductCommands)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").value(nullValue()))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].brand").value("brand"))
                .andExpect(jsonPath("$[0].price").value(getDefaultPrice()))
                .andExpect(jsonPath("$[0].type").value("type"))
                .andExpect(jsonPath("$[0].availableAmount").value(getDefaultAvailableAmount()))
                .andExpect(jsonPath("$[0].customizations[0].id").value(nullValue()))
                .andExpect(jsonPath("$[0].customizations[0].name").value("name"))
                .andExpect(jsonPath("$[0].customizations[0].multipleChoice").value(true))
                .andExpect(jsonPath("$[0].customizations[0].options[0].id").value(nullValue()))
                .andExpect(jsonPath("$[0].customizations[0].options[0].name").value("name"))
                .andExpect(jsonPath("$[0].customizations[0].options[0].defaultOption").value(false))
                .andExpect(jsonPath("$[0].customizations[0].options[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$[0].customizations[0].options[1].id").value(nullValue()))
                .andExpect(jsonPath("$[0].customizations[0].options[1].name").value("name"))
                .andExpect(jsonPath("$[0].customizations[0].options[1].defaultOption").value(false))
                .andExpect(jsonPath("$[0].customizations[0].options[1].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$[0].customizations[1].id").value(nullValue()))
                .andExpect(jsonPath("$[0].customizations[1].name").value("name"))
                .andExpect(jsonPath("$[0].customizations[1].multipleChoice").value(true))
                .andExpect(jsonPath("$[0].customizations[1].options[0].id").value(nullValue()))
                .andExpect(jsonPath("$[0].customizations[1].options[0].name").value("name"))
                .andExpect(jsonPath("$[0].customizations[1].options[0].defaultOption").value(false))
                .andExpect(jsonPath("$[0].customizations[1].options[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$[0].customizations[1].options[1].id").value(nullValue()))
                .andExpect(jsonPath("$[0].customizations[1].options[1].name").value("name"))
                .andExpect(jsonPath("$[0].customizations[1].options[1].defaultOption").value(false))
                .andExpect(jsonPath("$[0].customizations[1].options[1].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$[1].id").value(nullValue()))
                .andExpect(jsonPath("$[1].name").value("name"))
                .andExpect(jsonPath("$[1].brand").value("brand"))
                .andExpect(jsonPath("$[1].price").value(getDefaultPrice()))
                .andExpect(jsonPath("$[1].type").value("type"))
                .andExpect(jsonPath("$[1].availableAmount").value(getDefaultAvailableAmount()))
                .andExpect(jsonPath("$[1].customizations[0].id").value(nullValue()))
                .andExpect(jsonPath("$[1].customizations[0].name").value("name"))
                .andExpect(jsonPath("$[1].customizations[0].multipleChoice").value(true))
                .andExpect(jsonPath("$[1].customizations[0].options[0].id").value(nullValue()))
                .andExpect(jsonPath("$[1].customizations[0].options[0].name").value("name"))
                .andExpect(jsonPath("$[1].customizations[0].options[0].defaultOption").value(false))
                .andExpect(jsonPath("$[1].customizations[0].options[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$[1].customizations[0].options[1].id").value(nullValue()))
                .andExpect(jsonPath("$[1].customizations[0].options[1].name").value("name"))
                .andExpect(jsonPath("$[1].customizations[0].options[1].defaultOption").value(false))
                .andExpect(jsonPath("$[1].customizations[0].options[1].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$[1].customizations[1].id").value(nullValue()))
                .andExpect(jsonPath("$[1].customizations[1].name").value("name"))
                .andExpect(jsonPath("$[1].customizations[1].multipleChoice").value(true))
                .andExpect(jsonPath("$[1].customizations[1].options[0].id").value(nullValue()))
                .andExpect(jsonPath("$[1].customizations[1].options[0].name").value("name"))
                .andExpect(jsonPath("$[1].customizations[1].options[0].defaultOption").value(false))
                .andExpect(jsonPath("$[1].customizations[1].options[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$[1].customizations[1].options[1].id").value(nullValue()))
                .andExpect(jsonPath("$[1].customizations[1].options[1].name").value("name"))
                .andExpect(jsonPath("$[1].customizations[1].options[1].defaultOption").value(false))
                .andExpect(jsonPath("$[1].customizations[1].options[1].priceDifference").value(getDefaultPriceDifference()));
    }

    @Test
    void getProducts_ReturnsPageableContentDtoWithStatus200() throws Exception {
        Pageable pageable = PageRequest.of(0, 3);
        PageableContent<Product> productPageableContent = getProductPageableContent(pageable);
        ProductFilter filter = ProductFilter.builder().build();
        when(getProducts.getAll(filter, pageable.getPageNumber(), pageable.getPageSize())).thenReturn(productPageableContent);
        mockMvc.perform(get("/products")
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.pageNumber").value(pageable.getPageNumber()))
                .andExpect(jsonPath("$.pageSize").value(pageable.getPageSize()))
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
                .andExpect(jsonPath("$.elements[0].customizations[0].options[1].id").value(1))
                .andExpect(jsonPath("$.elements[0].customizations[0].options[1].name").value("name"))
                .andExpect(jsonPath("$.elements[0].customizations[0].options[1].defaultOption").value(false))
                .andExpect(jsonPath("$.elements[0].customizations[0].options[1].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.elements[0].customizations[1].id").value(1))
                .andExpect(jsonPath("$.elements[0].customizations[1].name").value("name"))
                .andExpect(jsonPath("$.elements[0].customizations[1].multipleChoice").value(true))
                .andExpect(jsonPath("$.elements[0].customizations[1].options[0].id").value(1))
                .andExpect(jsonPath("$.elements[0].customizations[1].options[0].name").value("name"))
                .andExpect(jsonPath("$.elements[0].customizations[1].options[0].defaultOption").value(false))
                .andExpect(jsonPath("$.elements[0].customizations[1].options[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.elements[0].customizations[1].options[1].id").value(1))
                .andExpect(jsonPath("$.elements[0].customizations[1].options[1].name").value("name"))
                .andExpect(jsonPath("$.elements[0].customizations[1].options[1].defaultOption").value(false))
                .andExpect(jsonPath("$.elements[0].customizations[1].options[1].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.elements[1].id").value(1))
                .andExpect(jsonPath("$.elements[1].name").value("name"))
                .andExpect(jsonPath("$.elements[1].brand").value("brand"))
                .andExpect(jsonPath("$.elements[1].price").value(getDefaultPrice()))
                .andExpect(jsonPath("$.elements[1].type").value("type"))
                .andExpect(jsonPath("$.elements[1].availableAmount").value(getDefaultAvailableAmount()))
                .andExpect(jsonPath("$.elements[1].customizations[0].id").value(1))
                .andExpect(jsonPath("$.elements[1].customizations[0].name").value("name"))
                .andExpect(jsonPath("$.elements[1].customizations[0].multipleChoice").value(true))
                .andExpect(jsonPath("$.elements[1].customizations[0].options[0].id").value(1))
                .andExpect(jsonPath("$.elements[1].customizations[0].options[0].name").value("name"))
                .andExpect(jsonPath("$.elements[1].customizations[0].options[0].defaultOption").value(false))
                .andExpect(jsonPath("$.elements[1].customizations[0].options[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.elements[1].customizations[0].options[1].id").value(1))
                .andExpect(jsonPath("$.elements[1].customizations[0].options[1].name").value("name"))
                .andExpect(jsonPath("$.elements[1].customizations[0].options[1].defaultOption").value(false))
                .andExpect(jsonPath("$.elements[1].customizations[0].options[1].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.elements[1].customizations[1].id").value(1))
                .andExpect(jsonPath("$.elements[1].customizations[1].name").value("name"))
                .andExpect(jsonPath("$.elements[1].customizations[1].multipleChoice").value(true))
                .andExpect(jsonPath("$.elements[1].customizations[1].options[0].id").value(1))
                .andExpect(jsonPath("$.elements[1].customizations[1].options[0].name").value("name"))
                .andExpect(jsonPath("$.elements[1].customizations[1].options[0].defaultOption").value(false))
                .andExpect(jsonPath("$.elements[1].customizations[1].options[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.elements[1].customizations[1].options[1].id").value(1))
                .andExpect(jsonPath("$.elements[1].customizations[1].options[1].name").value("name"))
                .andExpect(jsonPath("$.elements[1].customizations[1].options[1].defaultOption").value(false))
                .andExpect(jsonPath("$.elements[1].customizations[1].options[1].priceDifference").value(getDefaultPriceDifference()));
    }

    @Test
    void getProductById_ReturnsProductDtoWithStatus200() throws Exception {
        Long id = 1L;
        Product product = getProduct();
        when(getDetailedProduct.getById(id)).thenReturn(product);
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
                .andExpect(jsonPath("$.details[1].id").value(1))
                .andExpect(jsonPath("$.details[1].label").value("label"))
                .andExpect(jsonPath("$.details[1].description").value("description"))
                .andExpect(jsonPath("$.customizations[0].id").value(1))
                .andExpect(jsonPath("$.customizations[0].name").value("name"))
                .andExpect(jsonPath("$.customizations[0].multipleChoice").value(true))
                .andExpect(jsonPath("$.customizations[0].options[0].id").value(1))
                .andExpect(jsonPath("$.customizations[0].options[0].name").value("name"))
                .andExpect(jsonPath("$.customizations[0].options[0].defaultOption").value(false))
                .andExpect(jsonPath("$.customizations[0].options[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.customizations[0].options[1].id").value(1))
                .andExpect(jsonPath("$.customizations[0].options[1].name").value("name"))
                .andExpect(jsonPath("$.customizations[0].options[1].defaultOption").value(false))
                .andExpect(jsonPath("$.customizations[0].options[1].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.customizations[1].id").value(1))
                .andExpect(jsonPath("$.customizations[1].name").value("name"))
                .andExpect(jsonPath("$.customizations[1].multipleChoice").value(true))
                .andExpect(jsonPath("$.customizations[1].options[0].id").value(1))
                .andExpect(jsonPath("$.customizations[1].options[0].name").value("name"))
                .andExpect(jsonPath("$.customizations[1].options[0].defaultOption").value(false))
                .andExpect(jsonPath("$.customizations[1].options[0].priceDifference").value(getDefaultPriceDifference()))
                .andExpect(jsonPath("$.customizations[1].options[1].id").value(1))
                .andExpect(jsonPath("$.customizations[1].options[1].name").value("name"))
                .andExpect(jsonPath("$.customizations[1].options[1].defaultOption").value(false))
                .andExpect(jsonPath("$.customizations[1].options[1].priceDifference").value(getDefaultPriceDifference()));
    }

    @Test
    void checkProductsAvailableAmounts_ChecksStocksAndReturnsStatus204() throws Exception {
        Long productId = 1L;
        Integer requiredStock = 5;

        mockMvc.perform(get("/products/{productId}/available-amount", productId)
                        .queryParam("requiredStock", String.valueOf(requiredStock)))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(checkProductsStock, times(1)).checkStock(productId, requiredStock);
    }

    @Test
    void updateProductsAvailableAmounts_UpdatesStocksAndReturnsStatus204() throws Exception {
        Map<Long, Integer> productStockMap = Map.of(1L, 1, 2L, 2);

        mockMvc.perform(patch("/products/available-amount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productStockMap)))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(updateProductsStock, times(1)).updateStock(productStockMap);
    }

    @Test
    void updateProduct_UpdatesProductAndReturnsStatus204() throws Exception {
        Long id = 1L;
        UpsertProductCommand upsertProductCommand = getUpsertProductCommand();
        Product product = productMapper.commandToModel(upsertProductCommand);

        mockMvc.perform(patch("/products/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(upsertProductCommand)))
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(updateProduct, times(1)).update(product, id);
    }

    @Test
    void deleteProduct_DeletesProductAndReturnsStatus204() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/products/{id}", id))
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(deleteProduct, times(1)).delete(id);
    }
}
