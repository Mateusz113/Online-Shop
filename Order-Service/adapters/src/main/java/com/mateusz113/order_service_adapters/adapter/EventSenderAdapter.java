package com.mateusz113.order_service_adapters.adapter;

import com.mateusz113.order_service_core.port.outgoing.EventSender;
import com.mateusz113.order_service_model.event.EventType;
import com.mateusz113.order_service_model.order.OrderProcessingData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventSenderAdapter implements EventSender {
    @Value("${generate.invoice.topic}")
    private String generateInvoiceTopic;
    @Value("${send.email.topic}")
    private String sendEmailTopic;
    @Value("${send.email.with.invoice.topic}")
    private String sendEmailWithInvoiceTopic;
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendEvent(OrderProcessingData processingData) {
        kafkaTemplate.send(getTopicFromEventType(processingData.eventType()), processingData);
    }

    private String getTopicFromEventType(EventType eventType) {
        return switch (eventType) {
            case GENERATE_INVOICE -> generateInvoiceTopic;
            case SEND_EMAIL -> sendEmailTopic;
            case SEND_EMAIL_WITH_INVOICE -> sendEmailWithInvoiceTopic;
        };
    }
}
