package com.mateusz113.cart_service_adapters.config;

import feign.FeignException;
import feign.RetryableException;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ProductServiceClientConfig {
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100, 1000, 3);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            log.info("Feign call failed. Status: {}", response.status());
            FeignException exception = FeignException.errorStatus(methodKey, response);
            boolean serviceIsUnavailable = response.status() == 503;
            return serviceIsUnavailable
                    ? new RetryableException(response.status(), exception.getMessage(), response.request().httpMethod(), exception, (Long) null, response.request())
                    : exception;
        };
    }
}
