package com.mateusz113.order_service_adapters.config;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.mateusz113.order_service_model_public.command.ProcessOrderCommand;
import com.mateusz113.order_service_model_public.command.UpdateOrderStatusCommand;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, UpdateOrderStatusCommand> statusConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(getConfigProperties());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UpdateOrderStatusCommand> statusKafkaListenerContainerFactory(
            ConsumerFactory<String, UpdateOrderStatusCommand> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, UpdateOrderStatusCommand> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, ProcessOrderCommand> processConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(getConfigProperties());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProcessOrderCommand> processKafkaListenerContainerFactory(
            ConsumerFactory<String, ProcessOrderCommand> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, ProcessOrderCommand> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    private Map<String, Object> getConfigProperties() {
        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return configProperties;
    }
}
