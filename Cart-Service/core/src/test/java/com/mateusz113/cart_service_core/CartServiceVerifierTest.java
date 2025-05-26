package com.mateusz113.cart_service_core;

import com.mateusz113.cart_service_model.customization.AppliedCustomization;
import com.mateusz113.cart_service_model.exception.AppliedCustomizationIllegalDataException;
import com.mateusz113.cart_service_model.exception.AppliedCustomizationOptionIllegalDataException;
import com.mateusz113.cart_service_model.exception.CustomizedProductIllegalDataException;
import com.mateusz113.cart_service_model.product.CustomizedProduct;
import com.mateusz113.cart_service_model.product.SourceProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.mateusz113.cart_service_core.util.CartServiceCoreTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

public class CartServiceVerifierTest {
    private CartServiceVerifier verifier;

    @BeforeEach
    void setUp() {
        verifier = new CartServiceVerifier(getClock());
    }

    @Test
    void verifyCustomizedProduct_SourceIdIsNull_ThrowsCustomizedProductIllegalDataException() {
        CustomizedProduct product = getCustomizedProduct();
        product.setSourceId(null);

        CustomizedProductIllegalDataException exception = assertThrows(CustomizedProductIllegalDataException.class, () -> verifier.verifyCustomizedProduct(product));

        assertEquals("Id of the product source information cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyCustomizedProduct_QuantityIsNull_ThrowsCustomizedProductIllegalDataException() {
        CustomizedProduct product = getCustomizedProduct();
        product.setQuantity(null);

        CustomizedProductIllegalDataException exception = assertThrows(CustomizedProductIllegalDataException.class, () -> verifier.verifyCustomizedProduct(product));

        assertEquals("Quantity cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyCustomizedProduct_AppliedCustomizationsAreNull_ThrowsCustomizedProductIllegalDataException() {
        CustomizedProduct product = getCustomizedProduct();
        product.setAppliedCustomizations(null);

        CustomizedProductIllegalDataException exception = assertThrows(CustomizedProductIllegalDataException.class, () -> verifier.verifyCustomizedProduct(product));

        assertEquals("Customizations cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyCustomizedProduct_SourceIdOfAppliedCustomizationIsNull_ThrowsAppliedCustomizationIllegalDataException() {
        CustomizedProduct product = getCustomizedProduct();
        product.getAppliedCustomizations().forEach(customization -> customization.setSourceId(null));

        AppliedCustomizationIllegalDataException exception = assertThrows(AppliedCustomizationIllegalDataException.class, () -> verifier.verifyCustomizedProduct(product));

        assertEquals("Id of the customization source information cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyCustomizedProduct_SourceIdOfAppliedCustomizationOptionIsNull_ThrowsAppliedCustomizationOptionIllegalDataException() {
        CustomizedProduct product = getCustomizedProduct();
        product.getAppliedCustomizations().forEach(customization -> customization.getAppliedOptions().forEach(option -> option.setSourceId(null)));

        AppliedCustomizationOptionIllegalDataException exception = assertThrows(AppliedCustomizationOptionIllegalDataException.class, () -> verifier.verifyCustomizedProduct(product));

        assertEquals("Id of the customization option source information cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyCustomizedProduct_QuantityIsNegative_ThrowsCustomizedProductIllegalDataException() {
        CustomizedProduct product = getCustomizedProduct();
        product.setQuantity(-1);

        CustomizedProductIllegalDataException exception = assertThrows(CustomizedProductIllegalDataException.class, () -> verifier.verifyCustomizedProduct(product));

        assertEquals("Quantity of product in cart has to be positive.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyCustomizedProduct_DataIsCorrect_DoesNotThrowException() {
        assertDoesNotThrow(() -> verifier.verifyCustomizedProduct(getCustomizedProduct()));
    }

    @Test
    void validateCustomizedProductAgainstSourceData_SourceAmountIsSmallerThanQuantity_ThrowsCustomizedProductIllegalDataException() {
        CustomizedProduct product = getCustomizedProduct();
        product.setQuantity(1000);

        CustomizedProductIllegalDataException exception = assertThrows(CustomizedProductIllegalDataException.class, () -> verifier.validateCustomizedProductAgainstSourceData(getSourceProduct(), product));

        assertEquals("Cart product quantity has to be less of equal to the available amount.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void validateCustomizedProductAgainstSourceData_ThereIsNoSourceCustomizationWithId_ThrowsAppliedCustomizationIllegalDataException() {
        CustomizedProduct product = getCustomizedProduct();
        product.getAppliedCustomizations().forEach(customization -> customization.setSourceId(15L));

        AppliedCustomizationIllegalDataException exception = assertThrows(AppliedCustomizationIllegalDataException.class, () -> verifier.validateCustomizedProductAgainstSourceData(getSourceProduct(), product));

        assertEquals("Customizations of sent products do not match source data.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void validateCustomizedProductAgainstSourceData_CustomizationIsSingleChoiceAndOptionsAmountIsNot1_ThrowsAppliedCustomizationIllegalDataException() {
        CustomizedProduct product = getCustomizedProduct();
        AppliedCustomization customization = getAppliedCustomization();
        customization.setMultipleChoice(false);
        customization.setAppliedOptions(List.of());
        product.setAppliedCustomizations(List.of(customization));
        SourceProduct sourceProduct = getSourceProduct();
        sourceProduct.getCustomizations().forEach(customizationElement -> customizationElement.setMultipleChoice(false));

        AppliedCustomizationIllegalDataException exception = assertThrows(AppliedCustomizationIllegalDataException.class, () -> verifier.validateCustomizedProductAgainstSourceData(sourceProduct, product));

        assertEquals("There has to be exact one customization option selected for non multiple choice customizations.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void validateCustomizedProductAgainstSourceData_ThereIsNoSourceCustomizationOptionWithId_ThrowsAppliedCustomizationOptionIllegalDataException() {
        CustomizedProduct product = getCustomizedProduct();
        product.getAppliedCustomizations().forEach(customization -> customization.getAppliedOptions().forEach(option -> option.setSourceId(15L)));

        AppliedCustomizationOptionIllegalDataException exception = assertThrows(AppliedCustomizationOptionIllegalDataException.class, () -> verifier.validateCustomizedProductAgainstSourceData(getSourceProduct(), product));

        assertEquals("Customizations options of sent products do not match source data.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void validateCustomizedProductAgainstSourceData_DataIsCorrect_DoesNotThrowException() {
        assertDoesNotThrow(() -> verifier.validateCustomizedProductAgainstSourceData(getSourceProduct(), getCustomizedProduct()));
    }
}
