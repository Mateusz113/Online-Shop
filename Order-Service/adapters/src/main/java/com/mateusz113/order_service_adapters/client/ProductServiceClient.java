package com.mateusz113.order_service_adapters.client;

import com.mateusz113.order_service_adapters.config.FeignConfig;
import com.mateusz113.order_service_adapters.fallback.ProductServiceClientFallbackFactory;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(
        name = "product-service-client",
        url = "${spring.cloud.openfeign.client.config.product-service-client.url}",
        configuration = FeignConfig.class,
        fallbackFactory = ProductServiceClientFallbackFactory.class
)
public interface ProductServiceClient {
    @GetMapping("/products/available-amount")
    Response checkProductsAvailability(@RequestBody Map<Long, Integer> productStockMap);

    @PatchMapping("/products/available-amount")
    void updateSoldProductsStock(@RequestBody Map<Long, Integer> productsStockMap);
}
