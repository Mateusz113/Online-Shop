package com.mateusz113.product_service_core;

import com.mateusz113.product_service_model.exception.CustomizationElementIllegalDataException;
import com.mateusz113.product_service_model.exception.CustomizationOptionIllegalDataException;
import com.mateusz113.product_service_model.exception.ProductDetailIllegalDataException;
import com.mateusz113.product_service_model.exception.ProductFilterIllegalDataException;
import com.mateusz113.product_service_model.exception.ProductIllegalDataException;
import com.mateusz113.product_service_model.filter.ProductFilter;
import com.mateusz113.product_service_model.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.Arrays;
import java.util.List;

import static com.mateusz113.product_service_core.util.ProductServiceCoreTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceVerifierTest {
    private ProductServiceVerifier verifier;

    @BeforeEach
    void setUp() {
        Clock clock = getClock();
        verifier = new ProductServiceVerifier(clock);
    }

    @Test
    void verifyProductId_IdIsNull_ThrowsProductIllegalDataException() {
        ProductIllegalDataException exception = assertThrows(ProductIllegalDataException.class, () -> verifier.verifyProductId(null));

        assertEquals("Product ID cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyProductId_IdIsCorrect_DoesNotThrowException() {
        Long id = 1L;

        assertDoesNotThrow(() -> verifier.verifyProductId(id));
    }

    @Test
    void verifyProduct_ProductIsNull_ThrowsProductIllegalDataException() {
        ProductIllegalDataException exception = assertThrows(ProductIllegalDataException.class, () -> verifier.verifyProduct(null));

        assertEquals("Product and its fields cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyProduct_ProductFieldsAreNull_ThrowsProductIllegalDataException() {
        Product product = getProduct();
        product.setName(null);

        ProductIllegalDataException exception = assertThrows(ProductIllegalDataException.class, () -> verifier.verifyProduct(product));

        assertEquals("Product and its fields cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyProduct_ProductDetailsAreNull_ThrowsProductDetailIllegalDataException() {
        Product product = getProduct();
        product.setDetails(Arrays.asList(null, null));

        ProductDetailIllegalDataException exception = assertThrows(ProductDetailIllegalDataException.class, () -> verifier.verifyProduct(product));

        assertEquals("Product detail and its fields cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyProduct_ProductDetailsFieldsAreNull_ThrowsProductDetailIllegalDataException() {
        Product product = getProduct();
        product.getDetails().forEach(productDetail -> productDetail.setLabel(null));

        ProductDetailIllegalDataException exception = assertThrows(ProductDetailIllegalDataException.class, () -> verifier.verifyProduct(product));

        assertEquals("Product detail and its fields cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyProduct_CustomizationElementsAreNull_ThrowsCustomizationElementIllegalDataException() {
        Product product = getProduct();
        product.setCustomizations(Arrays.asList(null, null));

        CustomizationElementIllegalDataException exception = assertThrows(CustomizationElementIllegalDataException.class, () -> verifier.verifyProduct(product));

        assertEquals("Customization element and its fields cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyProduct_CustomizationElementsFieldsAreNull_ThrowsCustomizationElementIllegalDataException() {
        Product product = getProduct();
        product.getCustomizations().forEach(customizationElement -> customizationElement.setName(null));

        CustomizationElementIllegalDataException exception = assertThrows(CustomizationElementIllegalDataException.class, () -> verifier.verifyProduct(product));

        assertEquals("Customization element and its fields cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyProduct_CustomizationOptionsAreNull_ThrowsCustomizationOptionIllegalDataException() {
        Product product = getProduct();
        product.getCustomizations().forEach(customizationElement -> customizationElement.setOptions(Arrays.asList(null, null)));

        CustomizationOptionIllegalDataException exception = assertThrows(CustomizationOptionIllegalDataException.class, () -> verifier.verifyProduct(product));

        assertEquals("Customization option and its fields cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyProduct_CustomizationOptionsFieldsAreNull_ThrowsCustomizationOptionIllegalDataException() {
        Product product = getProduct();
        product.getCustomizations().forEach(customizationElement -> {
            customizationElement.getOptions().forEach(customizationOption -> {
                customizationOption.setName(null);
            });
        });

        CustomizationOptionIllegalDataException exception = assertThrows(CustomizationOptionIllegalDataException.class, () -> verifier.verifyProduct(product));

        assertEquals("Customization option and its fields cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyProduct_PriceLessThan0_ThrowsProductIllegalDataException() {
        Product product = getProduct();
        product.setPrice(BigDecimal.valueOf(-1));

        ProductIllegalDataException exception = assertThrows(ProductIllegalDataException.class, () -> verifier.verifyProduct(product));

        assertEquals("Product price has to be positive.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyProduct_PriceHasMoreThan2DigitsAfterDecimalPoint_ThrowsProductIllegalDataException() {
        Product product = getProduct();
        product.setPrice(BigDecimal.valueOf(10.111));

        ProductIllegalDataException exception = assertThrows(ProductIllegalDataException.class, () -> verifier.verifyProduct(product));

        assertEquals("Product price has a maximum of 2 digits after decimal point.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyProduct_AvailableAmountIsLessThan0_ThrowsProductIllegalDataException() {
        Product product = getProduct();
        product.setAvailableAmount(-1);

        ProductIllegalDataException exception = assertThrows(ProductIllegalDataException.class, () -> verifier.verifyProduct(product));

        assertEquals("Product has to have a non-negative available amount.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyProduct_CustomizationsHaveMoreThan2DefaultOptions_ThrowsCustomizationElementIllegalDataException() {
        Product product = getProduct();
        product.getCustomizations().forEach(customizationElement -> {
            customizationElement.getOptions().forEach(customizationOption -> customizationOption.setDefaultOption(true));
        });

        CustomizationElementIllegalDataException exception = assertThrows(CustomizationElementIllegalDataException.class, () -> verifier.verifyProduct(product));

        assertEquals("There cannot be more than one default option for each customization.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());

    }

    @Test
    void verifyProduct_CustomizationIsNotMultipleChoiceWithoutDefaultValue_ThrowsCustomizationElementIllegalDataException() {
        Product product = getProduct();
        product.getCustomizations().forEach(customizationElement -> {
            customizationElement.setMultipleChoice(false);
            customizationElement.getOptions().forEach(customizationOption -> customizationOption.setDefaultOption(false));
        });

        CustomizationElementIllegalDataException exception = assertThrows(CustomizationElementIllegalDataException.class, () -> verifier.verifyProduct(product));

        assertEquals("There has to be a default option for single choice customizations.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());

    }

    @Test
    void verifyProduct_OptionPriceDifferenceIsLessThan0_ThrowsCustomizationOptionIllegalDataException() {
        Product product = getProduct();
        product.getCustomizations().forEach(customizationElement -> {
            customizationElement.getOptions().forEach(customizationOption -> customizationOption.setPriceDifference(BigDecimal.valueOf(-1)));
        });

        CustomizationOptionIllegalDataException exception = assertThrows(CustomizationOptionIllegalDataException.class, () -> verifier.verifyProduct(product));

        assertEquals("Customization price has to be non-negative.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyProduct_DataIsCorrect_DoesNotThrowException() {
        Product product = getProduct();

        assertDoesNotThrow(() -> verifier.verifyProduct(product));
    }

    @Test
    void verifyProductList_ListOfProductsIsNull_ThrowsProductIllegalDataException() {
        ProductIllegalDataException exception = assertThrows(ProductIllegalDataException.class, () -> verifier.verifyProductList(null));

        assertEquals("Product and its fields cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyProductList_CorrectDataIsPassed_DoesNotThrowException() {
        List<Product> products = List.of(getProduct(), getProduct());

        assertDoesNotThrow(() -> verifier.verifyProductList(products));
    }

    @Test
    void verifyProductFilter_FilterIsNull_ThrowsProductFilterIllegalDataException() {
        ProductFilterIllegalDataException exception = assertThrows(ProductFilterIllegalDataException.class, () -> verifier.verifyProductFilter(null));

        assertEquals("Product filter cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyProductFilter_MinPriceIsLessThan0_ThrowsProductFilterIllegalDataException() {
        ProductFilter filter = ProductFilter.builder()
                .minPrice(BigDecimal.valueOf(-1))
                .build();

        ProductFilterIllegalDataException exception = assertThrows(ProductFilterIllegalDataException.class, () -> verifier.verifyProductFilter(filter));

        assertEquals("Minimum price has to be greater or equal to 0.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyProductFilter_MaxPriceIsLessThan0_ThrowsProductFilterIllegalDataException() {
        ProductFilter filter = ProductFilter.builder()
                .maxPrice(BigDecimal.valueOf(-1))
                .build();

        ProductFilterIllegalDataException exception = assertThrows(ProductFilterIllegalDataException.class, () -> verifier.verifyProductFilter(filter));

        assertEquals("Maximum price has to be greater or equal to 0.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyProductFilter_MinPriceIsBiggerThanMaxPrice_ThrowsProductFilterIllegalDataException() {
        ProductFilter filter = ProductFilter.builder()
                .minPrice(BigDecimal.valueOf(10))
                .maxPrice(BigDecimal.valueOf(5))
                .build();

        ProductFilterIllegalDataException exception = assertThrows(ProductFilterIllegalDataException.class, () -> verifier.verifyProductFilter(filter));

        assertEquals("Minimum price has to be less or equal to maximum price.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyProductFilter_MinAvailableAmountIsLessThan0_ThrowsProductFilterIllegalDataException() {
        ProductFilter filter = ProductFilter.builder()
                .minAvailableAmount(-1)
                .build();

        ProductFilterIllegalDataException exception = assertThrows(ProductFilterIllegalDataException.class, () -> verifier.verifyProductFilter(filter));

        assertEquals("Minimum available amount has to be greater than 0.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyProductFilter_ProductFilterIsValid_DoesNotThrowException() {
        ProductFilter filter = ProductFilter.builder().build();

        assertDoesNotThrow(() -> verifier.verifyProductFilter(filter));
    }
}
