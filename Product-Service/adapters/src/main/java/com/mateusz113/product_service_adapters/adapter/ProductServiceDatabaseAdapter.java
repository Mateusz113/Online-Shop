package com.mateusz113.product_service_adapters.adapter;

import com.mateusz113.product_service_adapters.entity.ProductEntity;
import com.mateusz113.product_service_adapters.mapper.ProductMapper;
import com.mateusz113.product_service_adapters.repository.ProductEntityRepository;
import com.mateusz113.product_service_core.ports.outgoing.ProductServiceDatabase;
import com.mateusz113.product_service_model.content_management.PageableContent;
import com.mateusz113.product_service_model.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

@RequiredArgsConstructor
public class ProductServiceDatabaseAdapter implements ProductServiceDatabase {
    private final ProductEntityRepository repository;
    private final ProductMapper mapper;

    @Override
    public Product save(Product product) {
        ProductEntity entity = mapper.modelToEntity(product);
        entity = repository.save(entity);
        return mapper.entityToModel(entity);
    }

    @Override
    public void delete(Product product) {
        ProductEntity entity = mapper.modelToEntity(product);
        repository.delete(entity);
    }

    @Override
    public Optional<Product> findById(Long productId) {
        Optional<ProductEntity> entityOptional = repository.findById(productId);
        return entityOptional.map(mapper::entityToModel);
    }

    @Override
    public PageableContent<Product> findAll(int pageNumber, int pageSize) {
        Page<ProductEntity> entities = repository.findAll(PageRequest.of(pageNumber, pageSize));
        return PageableContent.<Product>builder()
                .totalElements(entities.getTotalElements())
                .totalPages(entities.getTotalPages())
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .elements(entities.get().map(mapper::entityToModel).toList())
                .build();
    }

    @Override
    public PageableContent<Product> findByType(String type, int pageNumber, int pageSize) {
        Page<ProductEntity> entities = repository.findByType(type, PageRequest.of(pageNumber, pageSize));
        return PageableContent.<Product>builder()
                .totalElements(entities.getTotalElements())
                .totalPages(entities.getTotalPages())
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .elements(entities.get().map(mapper::entityToModel).toList())
                .build();
    }
}
