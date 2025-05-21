package com.mateusz113.order_service_adapters.listener;

import com.mateusz113.order_service_adapters.mapper.OrderProcessDataMapper;
import com.mateusz113.order_service_adapters.mapper.OrderStatusUpdateMapper;
import com.mateusz113.order_service_core.port.incoming.OrderServiceEventPorts;
import com.mateusz113.order_service_model.order.OrderProcessingData;
import com.mateusz113.order_service_model.order.OrderStatusUpdateData;
import com.mateusz113.order_service_model_public.command.ProcessOrderCommand;
import com.mateusz113.order_service_model_public.command.UpdateOrderStatusCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderServiceKafkaListener {
    private final OrderServiceEventPorts eventPorts;
    private final OrderStatusUpdateMapper orderStatusUpdateMapper;
    private final OrderProcessDataMapper orderProcessDataMapper;

    @RetryableTopic(attempts = "5", backoff = @Backoff(delay = 5000L, multiplier = 1.5))
    @KafkaListener(topics = "${order.paid.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenOrderPaidTopic(UpdateOrderStatusCommand command) {
        OrderStatusUpdateData data = orderStatusUpdateMapper.commandToModel(command);
        eventPorts.orderPaidEvent(data);
    }

    @KafkaListener(topics = "${order.paid.topic.dlt}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenOrderPaidTopicDlt(UpdateOrderStatusCommand command) {
        OrderStatusUpdateData data = orderStatusUpdateMapper.commandToModel(command);
        eventPorts.orderPaidEventDlt(data);
    }

    @KafkaListener(topics = "${order.shipment.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenOrderShipmentTopic(UpdateOrderStatusCommand command) {
        OrderStatusUpdateData data = orderStatusUpdateMapper.commandToModel(command);
        eventPorts.orderShipmentEvent(data);
    }

    @KafkaListener(topics = "${order.delivered.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenOrderDeliveredTopic(UpdateOrderStatusCommand command) {
        OrderStatusUpdateData data = orderStatusUpdateMapper.commandToModel(command);
        eventPorts.orderDeliveredEvent(data);
    }

    @RetryableTopic(attempts = "5", backoff = @Backoff(delay = 5000L, multiplier = 1.5))
    @KafkaListener(topics = "${generate.invoice.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenGenerateInvoiceTopic(ProcessOrderCommand command) {
        OrderProcessingData data = orderProcessDataMapper.commandToModel(command);
        eventPorts.generateInvoiceEvent(data);
    }

    @KafkaListener(topics = "${generate.invoice.topic.dlt}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenGenerateInvoiceTopicDlt(ProcessOrderCommand command) {
        log.error("Could not generate an invoice for data: {}", command);
    }

    @RetryableTopic(attempts = "5", backoff = @Backoff(delay = 5000L, multiplier = 1.5))
    @KafkaListener(topics = "${send.email.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenSendEmailTopic(ProcessOrderCommand command) {
        OrderProcessingData data = orderProcessDataMapper.commandToModel(command);
        eventPorts.sendEmailEvent(data);
    }

    @KafkaListener(topics = "${send.email.topic.dlt}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenSendEmailTopicDlt(ProcessOrderCommand command) {
        log.error("Could not send an email for data: {}", command);
    }

    @RetryableTopic(attempts = "5", backoff = @Backoff(delay = 5000L, multiplier = 1.5))
    @KafkaListener(topics = "${send.email.with.invoice.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenSendEmailWithInvoiceTopic(ProcessOrderCommand command) {
        OrderProcessingData data = orderProcessDataMapper.commandToModel(command);
        eventPorts.sendEmailWithInvoiceEvent(data);
    }

    @KafkaListener(topics = "${send.email.with.invoice.topic.dlt}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenSendEmailWithInvoiceTopicDlt(ProcessOrderCommand command) {
        log.error("Could not send an email with invoice for data: {}", command);
    }
}
