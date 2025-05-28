package com.mateusz113.order_service_core.verifier;

import com.mateusz113.order_service_model.client.Address;
import com.mateusz113.order_service_model.client.Client;
import com.mateusz113.order_service_model.exception.AddressIllegalDataException;
import com.mateusz113.order_service_model.exception.ClientIllegalDataException;
import com.mateusz113.order_service_model.exception.OrderIllegalDataException;
import com.mateusz113.order_service_model.exception.OrderStatusUpdateIllegalDataException;
import com.mateusz113.order_service_model.exception.ProductIllegalDataException;
import com.mateusz113.order_service_model.order.Order;
import com.mateusz113.order_service_model.order.OrderStatusUpdateData;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.OffsetDateTime;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class OrderServiceVerifier {
    private final Clock clock;

    public void verifyOrderData(Order order) {
        if (isNull(order.getCartId())) {
            throw new OrderIllegalDataException("Cart ID cannot be null.", OffsetDateTime.now(clock));
        }
        if (isNull(order.getClientId())) {
            throw new OrderIllegalDataException("Client ID cannot be null.", OffsetDateTime.now(clock));
        }
    }

    public void verifyOrderStatusUpdateData(OrderStatusUpdateData updateData) {
        if (isNull(updateData.orderId())) {
            throw new OrderStatusUpdateIllegalDataException("Order ID cannot be null.", OffsetDateTime.now(clock));
        }
        verifyClientData(updateData.client());
    }

    public void verifyClientData(Client client) {
        if (isNull(client.firstName())) {
            throw new ClientIllegalDataException("First name of the client cannot be null.", OffsetDateTime.now(clock));
        }
        if (isNull(client.lastName())) {
            throw new ClientIllegalDataException("Last name of the client cannot be null.", OffsetDateTime.now(clock));
        }
        if (isNull(client.email())) {
            throw new ClientIllegalDataException("Email of the client cannot be null.", OffsetDateTime.now(clock));
        }
        if (isNull(client.phoneNumber())) {
            throw new ClientIllegalDataException("Phone number of the client cannot be null.", OffsetDateTime.now(clock));
        }
        verifyAddressData(client.address());
    }

    public void verifyAddressData(Address address) {
        if (isNull(address.country())) {
            throw new AddressIllegalDataException("Country in address cannot be null.", OffsetDateTime.now(clock));
        }
        if (isNull(address.city())) {
            throw new AddressIllegalDataException("City in address cannot be null.", OffsetDateTime.now(clock));
        }
        if (isNull(address.zipCode())) {
            throw new AddressIllegalDataException("Zip code in address cannot be null.", OffsetDateTime.now(clock));
        }
        if (isNull(address.street())) {
            throw new AddressIllegalDataException("Street in address cannot be null.", OffsetDateTime.now(clock));
        }
        if (isNull(address.buildingNumber())) {
            throw new AddressIllegalDataException("Building number in address cannot be null.", OffsetDateTime.now(clock));
        }
    }
}
