package com.mateusz113.cart_service_adapters.client;

import com.mateusz113.cart_service_model.exception.ProductServiceCommunicationErrorException;
import com.mateusz113.cart_service_model.exception.ProductServiceIsUnavailableException;
import com.mateusz113.cart_service_model.exception.SourceProductNotPresentException;
import com.mateusz113.cart_service_model.product.SourceProduct;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class ProductServiceClientFallbackFactory implements FallbackFactory<ProductServiceClient> {
    private final Clock clock;

    @Override
    public ProductServiceClient create(Throwable cause) {
        return new ProductServiceClient() {
            @Override
            public SourceProduct getProductById(Long id) {
                if (cause instanceof FeignException exception) {
                    if (exception.status() == 404) {
                        throw new SourceProductNotPresentException("Source product with id: %d does not exist.".formatted(id), OffsetDateTime.now(clock));
                    } else if (exception.status() == 503) {
                        throw new ProductServiceIsUnavailableException("Product service is currently unavailable.", OffsetDateTime.now(clock));
                    }
                }
                throw new ProductServiceCommunicationErrorException("Could not communicate with service to get source data.", OffsetDateTime.now(clock));
            }
        };
    }
}
