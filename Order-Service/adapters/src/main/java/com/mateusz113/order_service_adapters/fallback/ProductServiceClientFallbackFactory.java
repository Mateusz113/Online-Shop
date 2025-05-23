package com.mateusz113.order_service_adapters.fallback;

import com.mateusz113.order_service_adapters.client.ProductServiceClient;
import com.mateusz113.order_service_model.exception.ProductInsufficientStockException;
import com.mateusz113.order_service_model.exception.ProductNotInStockException;
import com.mateusz113.order_service_model.exception.ServiceCommunicationErrorException;
import feign.FeignException;
import feign.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductServiceClientFallbackFactory implements FallbackFactory<ProductServiceClient> {
    private final Clock clock;

    @Override
    public ProductServiceClient create(Throwable cause) {
        return new ProductServiceClient() {
            @Override
            public Response checkProductsAvailability(Map<Long, Integer> productStockMap) {
                if (cause instanceof FeignException exception) {
                    if (exception.status() == 404) {
                        throw new ProductNotInStockException("Could not verify products stock as they are not present.", OffsetDateTime.now(clock));
                    }
                    if (exception.status() == 409) {
                        return Response.builder().status(409).build();
                    }
                }
                throw new ServiceCommunicationErrorException("Could not communicate with product service.", OffsetDateTime.now(clock));
            }

            @Override
            public void updateSoldProductsStock(Map<Long, Integer> productsStockMap) {
                if (cause instanceof FeignException exception) {
                    if (exception.status() == 404) {
                        throw new ProductNotInStockException("Could not update products stock as they are not present.", OffsetDateTime.now(clock));
                    }
                    if (exception.status() == 409) {
                        throw new ProductInsufficientStockException("Could not order products as there is not enough available.", OffsetDateTime.now(clock));
                    }
                }
                throw new ServiceCommunicationErrorException("Could not communicate with product service.", OffsetDateTime.now(clock));
            }
        };
    }
}
