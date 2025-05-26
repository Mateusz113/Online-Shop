package com.mateusz113.cart_service_adapters.adapter;

import com.mateusz113.cart_service_adapters.client.ProductServiceClient;
import com.mateusz113.cart_service_model.customization.SourceCustomizationElement;
import com.mateusz113.cart_service_model.customization.SourceCustomizationOption;
import com.mateusz113.cart_service_model.product.SourceProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.mateusz113.cart_service_adapters.util.CartServiceAdaptersTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProductServiceCommunicatorAdapterTest {
    private ProductServiceCommunicatorAdapter communicatorAdapter;
    private ProductServiceClient client;

    @BeforeEach
    void setUp() {
        client = mock(ProductServiceClient.class);
        communicatorAdapter = new ProductServiceCommunicatorAdapter(client);
    }

    @Test
    void getProductSourceData_ReturnsSourceProduct() {
        Long productId = 1L;
        when(client.getProductById(productId)).thenReturn(getSourceProduct());

        SourceProduct result = communicatorAdapter.getProductSourceData(productId);

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
}
