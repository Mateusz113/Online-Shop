package com.mateusz113.order_service_core.port.outgoing;

import com.mateusz113.order_service_model.order.OrderProcessingData;

public interface EventSender {
    void sendEvent(OrderProcessingData processingData);
}
