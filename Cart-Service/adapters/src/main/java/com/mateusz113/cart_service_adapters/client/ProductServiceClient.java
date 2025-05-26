package com.mateusz113.cart_service_adapters.client;

import com.mateusz113.cart_service_adapters.config.ProductServiceClientConfig;
import com.mateusz113.cart_service_model.product.SourceProduct;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "product-service-client",
        url = "${spring.cloud.openfeign.client.config.product-service-client.url}",
        configuration = ProductServiceClientConfig.class,
        fallbackFactory = ProductServiceClientFallbackFactory.class
)
public interface ProductServiceClient {
    @GetMapping("/products/{id}")
    SourceProduct getProductById(@PathVariable Long id);
}
