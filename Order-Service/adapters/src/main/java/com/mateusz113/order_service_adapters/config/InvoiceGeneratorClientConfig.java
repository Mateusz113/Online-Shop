package com.mateusz113.order_service_adapters.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InvoiceGeneratorClientConfig {
    @Value("${invoice.generator.api.key}")
    private String invoiceGeneratorApiKey;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> requestTemplate.header("Authorization", "Bearer " + invoiceGeneratorApiKey);
    }
}
