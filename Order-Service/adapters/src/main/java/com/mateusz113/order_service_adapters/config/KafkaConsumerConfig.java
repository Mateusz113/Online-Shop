package com.mateusz113.order_service_adapters.config;


import com.mateusz113.order_service_model_public.command.ProcessOrderCommand;
import com.mateusz113.order_service_model_public.command.UpdateOrderStatusCommand;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(value = "kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, UpdateOrderStatusCommand> statusConsumerFactory() {
        JsonDeserializer<UpdateOrderStatusCommand> deserializer = new JsonDeserializer<>(UpdateOrderStatusCommand.class);
        deserializer.addTrustedPackages("*");
        Map<String, Object> properties = getCommonConfigProperties();
        return new DefaultKafkaConsumerFactory<>(properties, new StringDeserializer(), deserializer);
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
        JsonDeserializer<ProcessOrderCommand> deserializer = new JsonDeserializer<>(ProcessOrderCommand.class);
        deserializer.addTrustedPackages("*");
        Map<String, Object> properties = getCommonConfigProperties();
        return new DefaultKafkaConsumerFactory<>(properties, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProcessOrderCommand> processKafkaListenerContainerFactory(
            ConsumerFactory<String, ProcessOrderCommand> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, ProcessOrderCommand> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    private Map<String, Object> getCommonConfigProperties() {
        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        return configProperties;
    }
}
