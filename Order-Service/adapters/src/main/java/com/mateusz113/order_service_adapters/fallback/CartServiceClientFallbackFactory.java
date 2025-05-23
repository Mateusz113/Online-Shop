package com.mateusz113.order_service_adapters.fallback;

import com.mateusz113.order_service_adapters.client.CartServiceClient;
import com.mateusz113.order_service_model.exception.CartNotFoundException;
import com.mateusz113.order_service_model.exception.ServiceCommunicationErrorException;
import com.mateusz113.order_service_model_public.dto.CartDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartServiceClientFallbackFactory implements FallbackFactory<CartServiceClient> {
    private final Clock clock;

    @Override
    public CartServiceClient create(Throwable cause) {
        return new CartServiceClient() {
            @Override
            public CartDto getCartById(Long cartId) {
                if (cause instanceof FeignException exception) {
                    if (exception.status() == 404) {
                        throw new CartNotFoundException("Cart with id: %d was not found.".formatted(cartId), OffsetDateTime.now(clock));
                    }
                }
                throw new ServiceCommunicationErrorException("Could not communicate with cart service.", OffsetDateTime.now(clock));
            }

            @Override
            public void deleteCart(Long cartId) {
                log.error("Could not delete cart with id: {}", cartId);
            }
        };
    }
}
