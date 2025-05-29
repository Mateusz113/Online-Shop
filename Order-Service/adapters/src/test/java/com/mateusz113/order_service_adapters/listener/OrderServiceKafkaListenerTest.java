package com.mateusz113.order_service_adapters.listener;

import com.mateusz113.order_service_adapters.mapper.OrderProcessDataMapper;
import com.mateusz113.order_service_adapters.mapper.OrderStatusUpdateMapper;
import com.mateusz113.order_service_core.port.incoming.OrderServiceEventPorts;
import com.mateusz113.order_service_model.order.OrderProcessingData;
import com.mateusz113.order_service_model.order.OrderStatusUpdateData;
import com.mateusz113.order_service_model_public.command.ProcessOrderCommand;
import com.mateusz113.order_service_model_public.command.UpdateOrderStatusCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Duration;

import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getProcessOrderCommand;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getUpdateOrderStatusCommand;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("kafka-enabled")
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "${spring.kafka.bootstrap-servers}", topics = {
        "${order.paid.topic}", "${order.paid.topic.dlt}", "${order.shipment.topic}",
        "${order.delivered.topic}", "${generate.invoice.topic}", "${generate.invoice.topic.dlt}",
        "${send.email.topic}", "${send.email.topic.dlt}", "${send.email.with.invoice.topic}", "${send.email.with.invoice.topic.dlt}"
})
public class OrderServiceKafkaListenerTest {
    private static final Duration AWAIT_AMOUNT = Duration.ofSeconds(2);
    @Autowired
    private KafkaListenerEndpointRegistry registry;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private OrderStatusUpdateMapper statusUpdateMapper;
    @Autowired
    private OrderProcessDataMapper processDataMapper;
    @MockitoBean
    private OrderServiceEventPorts eventPorts;
    @MockitoSpyBean
    private OrderServiceKafkaListener listener;
    @Value("${order.paid.topic}")
    private String orderPaidTopic;
    @Value("${order.paid.topic.dlt}")
    private String orderPaidTopicDlt;
    @Value("${order.shipment.topic}")
    private String orderShipmentTopic;
    @Value("${order.delivered.topic}")
    private String orderDeliveredTopic;
    @Value("${generate.invoice.topic}")
    private String generateInvoiceTopic;
    @Value("${generate.invoice.topic.dlt}")
    private String generateInvoiceTopicDlt;
    @Value("${send.email.topic}")
    private String sendEmailTopic;
    @Value("${send.email.topic.dlt}")
    private String sendEmailTopicDlt;
    @Value("${send.email.with.invoice.topic}")
    private String sendEmailWithInvoiceTopic;
    @Value("${send.email.with.invoice.topic.dlt}")
    private String sendEmailWithInvoiceTopicDlt;

    @BeforeEach
    void setUp() {
        // Wait for the listener containers to be assigned, before testing
        // Not doing it makes tests inconsistent
        for (MessageListenerContainer container : registry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(container, 1);
        }
    }

    @Test
    void listenOrderPaidTopic_ListensToTopicAndCallsEventHandler() {
        UpdateOrderStatusCommand command = getUpdateOrderStatusCommand();
        OrderStatusUpdateData updateData = statusUpdateMapper.commandToModel(command);

        kafkaTemplate.send(orderPaidTopic, command);

        await().atMost(AWAIT_AMOUNT).untilAsserted(() -> {
            verify(listener).listenOrderPaidTopic(command);
            verify(eventPorts).orderPaidEvent(updateData);
        });
    }

    @Test
    void listenOrderPaidTopicDlt_ListensToTopicAndCallsEventHandler() {
        UpdateOrderStatusCommand command = getUpdateOrderStatusCommand();
        OrderStatusUpdateData updateData = statusUpdateMapper.commandToModel(command);

        kafkaTemplate.send(orderPaidTopicDlt, command);

        await().atMost(AWAIT_AMOUNT).untilAsserted(() -> {
            verify(listener).listenOrderPaidTopicDlt(command);
            verify(eventPorts).orderPaidEventDlt(updateData);
        });
    }

    @Test
    void listenOrderShipmentTopic_ListensToTopicAndCallsEventHandler() {
        UpdateOrderStatusCommand command = getUpdateOrderStatusCommand();
        OrderStatusUpdateData updateData = statusUpdateMapper.commandToModel(command);

        kafkaTemplate.send(orderShipmentTopic, command);

        await().atMost(AWAIT_AMOUNT).untilAsserted(() -> {
            verify(listener).listenOrderShipmentTopic(command);
            verify(eventPorts).orderShipmentEvent(updateData);
        });
    }

    @Test
    void listenOrderDeliveredTopic_ListensToTopicAndCallsEventHandler() {
        UpdateOrderStatusCommand command = getUpdateOrderStatusCommand();
        OrderStatusUpdateData updateData = statusUpdateMapper.commandToModel(command);

        kafkaTemplate.send(orderDeliveredTopic, command);

        await().atMost(AWAIT_AMOUNT).untilAsserted(() -> {
            verify(listener).listenOrderDeliveredTopic(command);
            verify(eventPorts).orderDeliveredEvent(updateData);
        });
    }

    @Test
    void listenGenerateInvoiceTopic_ListensToTopicAndCallsEventHandler() {
        ProcessOrderCommand command = getProcessOrderCommand();
        OrderProcessingData data = processDataMapper.commandToModel(command);

        kafkaTemplate.send(generateInvoiceTopic, command);

        await().atMost(AWAIT_AMOUNT).untilAsserted(() -> {
            verify(listener).listenGenerateInvoiceTopic(command);
            verify(eventPorts).generateInvoiceEvent(data);
        });
    }

    @Test
    void listenGenerateInvoiceTopicDlt_ListensToTopic() {
        ProcessOrderCommand command = getProcessOrderCommand();

        kafkaTemplate.send(generateInvoiceTopicDlt, command);

        await().atMost(AWAIT_AMOUNT).untilAsserted(() -> verify(listener).listenGenerateInvoiceTopicDlt(command));
    }

    @Test
    void listenSendEmailTopic_ListensToTopicAndCallsEventHandler() {
        ProcessOrderCommand command = getProcessOrderCommand();
        OrderProcessingData data = processDataMapper.commandToModel(command);

        kafkaTemplate.send(sendEmailTopic, command);

        await().atMost(AWAIT_AMOUNT).untilAsserted(() -> {
            verify(listener).listenSendEmailTopic(command);
            verify(eventPorts).sendEmailEvent(data);
        });
    }

    @Test
    void listenSendEmailTopicDlt_ListensToTopic() {
        ProcessOrderCommand command = getProcessOrderCommand();

        kafkaTemplate.send(sendEmailTopicDlt, command);

        await().atMost(AWAIT_AMOUNT).untilAsserted(() -> verify(listener).listenSendEmailTopicDlt(command));
    }

    @Test
    void listenSendEmailWithInvoiceTopic_ListensToTopicAndCallsEventHandler() {
        ProcessOrderCommand command = getProcessOrderCommand();
        OrderProcessingData data = processDataMapper.commandToModel(command);

        kafkaTemplate.send(sendEmailWithInvoiceTopic, command);

        await().atMost(AWAIT_AMOUNT).untilAsserted(() -> {
            verify(listener).listenSendEmailWithInvoiceTopic(command);
            verify(eventPorts).sendEmailWithInvoiceEvent(data);
        });
    }

    @Test
    void listenSendEmailWithInvoiceTopicDlt_ListensToTopic() {
        ProcessOrderCommand command = getProcessOrderCommand();

        kafkaTemplate.send(sendEmailWithInvoiceTopicDlt, command);

        await().atMost(AWAIT_AMOUNT).untilAsserted(() -> verify(listener).listenSendEmailWithInvoiceTopicDlt(command));
    }
}
