package com.mateusz113.order_service_model.order;

import com.mateusz113.order_service_model.client.Client;
import com.mateusz113.order_service_model.event.EventType;
import lombok.Builder;

@Builder
public record OrderProcessingData(
        Long orderId,
        EventType eventType,
        Client client
) {
}
