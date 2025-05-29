package com.mateusz113.order_service_adapters.adapter;

import com.mateusz113.order_service_adapters.mapper.OrderProcessDataMapper;
import com.mateusz113.order_service_core.port.outgoing.EventSender;
import com.mateusz113.order_service_model.event.EventType;
import com.mateusz113.order_service_model.order.OrderProcessingData;
import com.mateusz113.order_service_model_public.command.ProcessOrderCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventSenderAdapter implements EventSender {
    private final OrderProcessDataMapper mapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${generate.invoice.topic}")
    private String generateInvoiceTopic;
    @Value("${send.email.topic}")
    private String sendEmailTopic;
    @Value("${send.email.with.invoice.topic}")
    private String sendEmailWithInvoiceTopic;

    @Override
    public void sendEvent(OrderProcessingData processingData) {
        ProcessOrderCommand processOrderCommand = mapper.modelToCommand(processingData);
        kafkaTemplate.send(getTopicFromEventType(processingData.eventType()), processOrderCommand);
    }

    private String getTopicFromEventType(EventType eventType) {
        return switch (eventType) {
            case GENERATE_INVOICE -> generateInvoiceTopic;
            case SEND_EMAIL -> sendEmailTopic;
            case SEND_EMAIL_WITH_INVOICE -> sendEmailWithInvoiceTopic;
        };
    }
}
