package com.mateusz113.product_service_adapters.config;

import com.mateusz113.product_service_adapters.adapter.ProductServiceDatabaseAdapter;
import com.mateusz113.product_service_adapters.mapper.ProductMapper;
import com.mateusz113.product_service_adapters.repository.ProductEntityRepository;
import com.mateusz113.product_service_core.ProductServiceFacade;
import com.mateusz113.product_service_core.ProductServiceVerifier;
import com.mateusz113.product_service_core.ports.incoming.AddNewProducts;
import com.mateusz113.product_service_core.ports.incoming.CheckProductsStock;
import com.mateusz113.product_service_core.ports.incoming.DeleteProduct;
import com.mateusz113.product_service_core.ports.incoming.GetDetailedProduct;
import com.mateusz113.product_service_core.ports.incoming.GetProducts;
import com.mateusz113.product_service_core.ports.incoming.UpdateProduct;
import com.mateusz113.product_service_core.ports.incoming.UpdateProductsStock;
import com.mateusz113.product_service_core.ports.outgoing.ProductServiceDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ProductServiceConfiguration {
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public ProductServiceDatabase productServiceDatabase(ProductEntityRepository repository, ProductMapper mapper) {
        return new ProductServiceDatabaseAdapter(repository, mapper);
    }

    @Bean
    public ProductServiceVerifier productServiceVerifier(Clock clock) {
        return new ProductServiceVerifier(clock);
    }

    @Bean
    public AddNewProducts addNewProducts(ProductServiceDatabase productServiceDatabase, ProductServiceVerifier verifier, Clock clock) {
        return new ProductServiceFacade(productServiceDatabase, verifier, clock);
    }

    @Bean
    public DeleteProduct deleteProduct(ProductServiceDatabase productServiceDatabase, ProductServiceVerifier verifier, Clock clock) {
        return new ProductServiceFacade(productServiceDatabase, verifier, clock);
    }

    @Bean
    public GetDetailedProduct getDetailedProduct(ProductServiceDatabase productServiceDatabase, ProductServiceVerifier verifier, Clock clock) {
        return new ProductServiceFacade(productServiceDatabase, verifier, clock);
    }

    @Bean
    public GetProducts getProducts(ProductServiceDatabase productServiceDatabase, ProductServiceVerifier verifier, Clock clock) {
        return new ProductServiceFacade(productServiceDatabase, verifier, clock);
    }

    @Bean
    public CheckProductsStock checkProductsStock(ProductServiceDatabase database, ProductServiceVerifier verifier, Clock clock) {
        return new ProductServiceFacade(database, verifier, clock);
    }

    @Bean
    public UpdateProductsStock updateProductsStock(ProductServiceDatabase database, ProductServiceVerifier verifier, Clock clock) {
        return new ProductServiceFacade(database, verifier, clock);
    }

    @Bean
    public UpdateProduct updateProduct(ProductServiceDatabase productServiceDatabase, ProductServiceVerifier verifier, Clock clock) {
        return new ProductServiceFacade(productServiceDatabase, verifier, clock);
    }
}
