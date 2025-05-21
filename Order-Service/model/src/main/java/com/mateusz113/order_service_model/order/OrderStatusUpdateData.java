package com.mateusz113.order_service_model.order;

import com.mateusz113.order_service_model.client.Client;
import lombok.Builder;

@Builder
public record OrderStatusUpdateData(
        Long orderId,
        Client client
) {
}
