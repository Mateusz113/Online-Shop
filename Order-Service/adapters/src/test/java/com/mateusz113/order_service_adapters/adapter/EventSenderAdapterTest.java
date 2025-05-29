package com.mateusz113.order_service_adapters.adapter;

import com.mateusz113.order_service_adapters.mapper.OrderProcessDataMapper;
import com.mateusz113.order_service_core.port.outgoing.EventSender;
import com.mateusz113.order_service_model.event.EventType;
import com.mateusz113.order_service_model.order.OrderProcessingData;
import com.mateusz113.order_service_model_public.command.ProcessOrderCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getOrderProcessingData;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class EventSenderAdapterTest {
    private EventSender eventSender;
    private OrderProcessDataMapper mapper;
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${generate.invoice.topic}")
    private String generateInvoiceTopic;
    @Value("${send.email.topic}")
    private String sendEmailTopic;
    @Value("${send.email.with.invoice.topic}")
    private String sendEmailWithInvoiceTopic;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(String.valueOf(KafkaTemplate.class));
        mapper = Mappers.getMapper(OrderProcessDataMapper.class);
        eventSender = new EventSenderAdapter(mapper, kafkaTemplate);
    }

    @Test
    void sendEvent_EventTypeIsGenerateInvoice_SendsEventToGenerateInvoiceTopic() {
        OrderProcessingData data = getOrderProcessingData(EventType.GENERATE_INVOICE);
        ProcessOrderCommand command = mapper.modelToCommand(data);

        eventSender.sendEvent(data);

        verify(kafkaTemplate, times(1)).send(eq(generateInvoiceTopic), eq(command));
    }

    @Test
    void sendEvent_EventTypeIsSendEmail_SendsEventToSendEmailTopic() {
        OrderProcessingData data = getOrderProcessingData(EventType.SEND_EMAIL);
        ProcessOrderCommand command = mapper.modelToCommand(data);

        eventSender.sendEvent(data);

        verify(kafkaTemplate, times(1)).send(eq(sendEmailTopic), eq(command));
    }

    @Test
    void sendEvent_EventTypeIsSendEmailWithInvoice_SendsEventToSendEmailWithInvoiceTopic() {
        OrderProcessingData data = getOrderProcessingData(EventType.SEND_EMAIL_WITH_INVOICE);
        ProcessOrderCommand command = mapper.modelToCommand(data);

        eventSender.sendEvent(data);

        verify(kafkaTemplate, times(1)).send(eq(sendEmailWithInvoiceTopic), eq(command));
    }
}
