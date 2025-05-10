package com.mateusz113.cart_service_adapters.config;

import com.mateusz113.cart_service_adapters.adapter.CartServiceDatabaseAdapter;
import com.mateusz113.cart_service_adapters.adapter.ProductServiceCommunicatorAdapter;
import com.mateusz113.cart_service_adapters.client.ProductServiceClient;
import com.mateusz113.cart_service_adapters.mapper.CartMapper;
import com.mateusz113.cart_service_adapters.mapper.CustomizedProductMapper;
import com.mateusz113.cart_service_adapters.repository.CartRepository;
import com.mateusz113.cart_service_adapters.repository.CustomizedProductRepository;
import com.mateusz113.cart_service_core.CartServiceFacade;
import com.mateusz113.cart_service_core.CartServiceVerifier;
import com.mateusz113.cart_service_core.ports.incoming.AddCart;
import com.mateusz113.cart_service_core.ports.incoming.AddProduct;
import com.mateusz113.cart_service_core.ports.incoming.DeleteCart;
import com.mateusz113.cart_service_core.ports.incoming.DeleteProduct;
import com.mateusz113.cart_service_core.ports.incoming.GetCart;
import com.mateusz113.cart_service_core.ports.outgoing.CartServiceDatabase;
import com.mateusz113.cart_service_core.ports.outgoing.ProductServiceCommunicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class CartServiceConfig {
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public AddCart addCart(
            CartServiceDatabase database,
            ProductServiceCommunicator productServiceCommunicator,
            CartServiceVerifier verifier,
            Clock clock
    ) {
        return new CartServiceFacade(database, productServiceCommunicator, verifier, clock);
    }

    @Bean
    public AddProduct addProduct(
            CartServiceDatabase database,
            ProductServiceCommunicator productServiceCommunicator,
            CartServiceVerifier verifier,
            Clock clock
    ) {
        return new CartServiceFacade(database, productServiceCommunicator, verifier, clock);
    }

    @Bean
    public DeleteCart deleteCart(
            CartServiceDatabase database,
            ProductServiceCommunicator productServiceCommunicator,
            CartServiceVerifier verifier,
            Clock clock
    ) {
        return new CartServiceFacade(database, productServiceCommunicator, verifier, clock);
    }

    @Bean
    public DeleteProduct deleteProduct(
            CartServiceDatabase database,
            ProductServiceCommunicator productServiceCommunicator,
            CartServiceVerifier verifier,
            Clock clock
    ) {
        return new CartServiceFacade(database, productServiceCommunicator, verifier, clock);
    }

    @Bean
    public GetCart getCart(
            CartServiceDatabase database,
            ProductServiceCommunicator productServiceCommunicator,
            CartServiceVerifier verifier,
            Clock clock
    ) {
        return new CartServiceFacade(database, productServiceCommunicator, verifier, clock);
    }

    @Bean
    public CartServiceVerifier cartServiceVerifier(Clock clock) {
        return new CartServiceVerifier(clock);
    }

    @Bean
    public CartServiceDatabase cartServiceDatabase(
            CartRepository cartRepository,
            CustomizedProductRepository customizedProductRepository,
            CartMapper cartMapper,
            CustomizedProductMapper customizedProductMapper
    ) {
        return new CartServiceDatabaseAdapter(cartRepository, customizedProductRepository, cartMapper, customizedProductMapper);
    }

    @Bean
    public ProductServiceCommunicator productServiceCommunicator(
            ProductServiceClient productServiceClient
    ) {
        return new ProductServiceCommunicatorAdapter(productServiceClient);
    }
}
