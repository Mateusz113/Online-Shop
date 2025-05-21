package com.mateusz113.order_service_adapters.client;

import com.mateusz113.order_service_adapters.config.FeignConfig;
import com.mateusz113.order_service_adapters.fallback.CartServiceClientFallbackFactory;
import com.mateusz113.order_service_model_public.dto.CartDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "cart-service-client",
        url = "${spring.cloud.openfeign.client.config.cart-service-client.url}",
        configuration = FeignConfig.class,
        fallbackFactory = CartServiceClientFallbackFactory.class
)
public interface CartServiceClient {
    @GetMapping("/carts/{cartId}")
    CartDto getCartById(@PathVariable Long cartId);

    @DeleteMapping("/carts/{cartId}")
    void deleteCart(@PathVariable Long cartId);
}
