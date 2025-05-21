package com.mateusz113.order_service_core.port.incoming;

import com.mateusz113.order_service_model.order.OrderProcessingData;
import com.mateusz113.order_service_model.order.OrderStatusUpdateData;

public interface OrderServiceEventPorts {
    void orderPaidEvent(OrderStatusUpdateData updateData);

    void orderPaidEventDlt(OrderStatusUpdateData updateData);

    void orderShipmentEvent(OrderStatusUpdateData updateData);

    void orderDeliveredEvent(OrderStatusUpdateData updateData);

    void generateInvoiceEvent(OrderProcessingData processingData);

    void sendEmailEvent(OrderProcessingData processingData);

    void sendEmailWithInvoiceEvent(OrderProcessingData processingData);
}
