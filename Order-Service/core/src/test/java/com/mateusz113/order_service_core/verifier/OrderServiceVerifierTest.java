package com.mateusz113.order_service_core.verifier;

import com.mateusz113.order_service_model.client.Address;
import com.mateusz113.order_service_model.client.Client;
import com.mateusz113.order_service_model.exception.AddressIllegalDataException;
import com.mateusz113.order_service_model.exception.ClientIllegalDataException;
import com.mateusz113.order_service_model.exception.OrderIllegalDataException;
import com.mateusz113.order_service_model.exception.OrderStatusUpdateIllegalDataException;
import com.mateusz113.order_service_model.order.Order;
import com.mateusz113.order_service_model.order.OrderStatusUpdateData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static com.mateusz113.order_service_core.util.OrderServiceCoreUtil.getAddress;
import static com.mateusz113.order_service_core.util.OrderServiceCoreUtil.getAddressCustomized;
import static com.mateusz113.order_service_core.util.OrderServiceCoreUtil.getClient;
import static com.mateusz113.order_service_core.util.OrderServiceCoreUtil.getClientCustomized;
import static com.mateusz113.order_service_core.util.OrderServiceCoreUtil.getClock;
import static com.mateusz113.order_service_core.util.OrderServiceCoreUtil.getDefaultTime;
import static com.mateusz113.order_service_core.util.OrderServiceCoreUtil.getOrder;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderServiceVerifierTest {
    private OrderServiceVerifier verifier;

    @BeforeEach
    void setUp() {
        Clock clock = getClock();
        verifier = new OrderServiceVerifier(clock);
    }

    @Test
    void verifyOrderData_CartIdIsNull_ThrowsOrderIllegalDataException() {
        Order order = getOrder();
        order.setCartId(null);

        OrderIllegalDataException exception = assertThrows(OrderIllegalDataException.class, () -> verifier.verifyOrderData(order));

        assertEquals("Cart ID cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyOrderData_ClientIdIsNull_ThrowsOrderIllegalDataException() {
        Order order = getOrder();
        order.setClientId(null);

        OrderIllegalDataException exception = assertThrows(OrderIllegalDataException.class, () -> verifier.verifyOrderData(order));

        assertEquals("Client ID cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyOrderData_DataIsCorrect_DoesNotThrow() {
        Order order = getOrder();

        assertDoesNotThrow(() -> verifier.verifyOrderData(order));
    }

    @Test
    void verifyOrderStatusUpdateData_OrderIdIsNull_ThrowsOrderStatusUpdateIllegalDataException() {
        OrderStatusUpdateData statusUpdateData = OrderStatusUpdateData.builder()
                .orderId(null)
                .client(getClient())
                .build();

        OrderStatusUpdateIllegalDataException exception = assertThrows(OrderStatusUpdateIllegalDataException.class, () -> verifier.verifyOrderStatusUpdateData(statusUpdateData));

        assertEquals("Order ID cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyOrderStatusUpdateData_DataIsCorrect_DoesNotThrow() {
        OrderStatusUpdateData statusUpdateData = OrderStatusUpdateData.builder()
                .orderId(1L)
                .client(getClient())
                .build();

        assertDoesNotThrow(() -> verifier.verifyOrderStatusUpdateData(statusUpdateData));
    }

    @Test
    void verifyClientData_FirstNameIsNull_ThrowsClientIllegalDataException() {
        Client client = getClientCustomized(null, "lastName", "email", "phoneNumber");

        ClientIllegalDataException exception = assertThrows(ClientIllegalDataException.class, () -> verifier.verifyClientData(client));

        assertEquals("First name of the client cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }


    @Test
    void verifyClientData_LastNameIsNull_ThrowsClientIllegalDataException() {
        Client client = getClientCustomized("firstName", null, "email", "phoneNumber");

        ClientIllegalDataException exception = assertThrows(ClientIllegalDataException.class, () -> verifier.verifyClientData(client));

        assertEquals("Last name of the client cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyClientData_EmailIsNull_ThrowsClientIllegalDataException() {
        Client client = getClientCustomized("firstName", "lastName", null, "phoneNumber");

        ClientIllegalDataException exception = assertThrows(ClientIllegalDataException.class, () -> verifier.verifyClientData(client));

        assertEquals("Email of the client cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyClientData_PhoneNumberIsNull_ThrowsClientIllegalDataException() {
        Client client = getClientCustomized("firstName", "lastName", "email", null);

        ClientIllegalDataException exception = assertThrows(ClientIllegalDataException.class, () -> verifier.verifyClientData(client));

        assertEquals("Phone number of the client cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyClientData_DataIsCorrect_DoesNotThrow() {
        Client client = getClient();

        assertDoesNotThrow(() -> verifier.verifyClientData(client));
    }

    @Test
    void verifyAddressData_CountryIsNull_ThrowsAddressIllegalDataException() {
        Address address = getAddressCustomized(null, "city", "zipCode", "street", "buildingNumber", "apartmentNumber");

        AddressIllegalDataException exception = assertThrows(AddressIllegalDataException.class, () -> verifier.verifyAddressData(address));

        assertEquals("Country in address cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyAddressData_CityIsNull_ThrowsAddressIllegalDataException() {
        Address address = getAddressCustomized("country", null, "zipCode", "street", "buildingNumber", "apartmentNumber");

        AddressIllegalDataException exception = assertThrows(AddressIllegalDataException.class, () -> verifier.verifyAddressData(address));

        assertEquals("City in address cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyAddressData_ZipCodeIsNull_ThrowsAddressIllegalDataException() {
        Address address = getAddressCustomized("country", "city", null, "street", "buildingNumber", "apartmentNumber");

        AddressIllegalDataException exception = assertThrows(AddressIllegalDataException.class, () -> verifier.verifyAddressData(address));

        assertEquals("Zip code in address cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyAddressData_StreetIsNull_ThrowsAddressIllegalDataException() {
        Address address = getAddressCustomized("country", "city", "zipCode", null, "buildingNumber", "apartmentNumber");

        AddressIllegalDataException exception = assertThrows(AddressIllegalDataException.class, () -> verifier.verifyAddressData(address));

        assertEquals("Street in address cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyAddressData_BuildingNumberIsNull_ThrowsAddressIllegalDataException() {
        Address address = getAddressCustomized("country", "city", "zipCode", "street", null, "apartmentNumber");

        AddressIllegalDataException exception = assertThrows(AddressIllegalDataException.class, () -> verifier.verifyAddressData(address));

        assertEquals("Building number in address cannot be null.", exception.getMessage());
        assertEquals(400, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void verifyAddressData_DataIsCorrect_DoesNotThrow() {
        Address address = getAddress();

        assertDoesNotThrow(() -> verifier.verifyAddressData(address));
    }
}
