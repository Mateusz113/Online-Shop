package com.mateusz113.order_service_model.order;

import com.mateusz113.order_service_model.client.Client;
import com.mateusz113.order_service_model.event.EventType;
import lombok.Builder;

@Builder
public record OrderProcessingData(
        Order order,
        EventType eventType,
        Client client
) {
}
