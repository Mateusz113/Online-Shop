package com.mateusz113.order_service_adapters.client;

import com.mateusz113.order_service_adapters.config.FeignConfig;
import com.mateusz113.order_service_adapters.fallback.ProductServiceClientFallbackFactory;
import com.mateusz113.order_service_model_public.command.UpdateProductsStocksCommand;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(
        name = "product-service-client",
        url = "${spring.cloud.openfeign.client.config.product-service-client.url}",
        configuration = FeignConfig.class,
        fallbackFactory = ProductServiceClientFallbackFactory.class
)
public interface ProductServiceClient {
    @GetMapping("/products/{productId}/available-amount")
    ResponseEntity<Void> checkProductsAvailability(@PathVariable Long productId, @RequestParam Integer requiredStock);

    @PatchMapping("/products/available-amount")
    void updateSoldProductsStock(@RequestBody UpdateProductsStocksCommand command);
}
