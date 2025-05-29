package com.mateusz113.product_service_adapters.adapter;

import com.mateusz113.product_service_adapters.entity.ProductEntity;
import com.mateusz113.product_service_adapters.mapper.ProductMapper;
import com.mateusz113.product_service_adapters.repository.ProductEntityRepository;
import com.mateusz113.product_service_core.ports.outgoing.ProductServiceDatabase;
import com.mateusz113.product_service_model.content_management.PageableContent;
import com.mateusz113.product_service_model.filter.ProductFilter;
import com.mateusz113.product_service_model.product.Product;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.mateusz113.product_service_adapters.specification.ProductSpecification.getSpecificationFromFilter;

@Component
@RequiredArgsConstructor
public class ProductServiceDatabaseAdapter implements ProductServiceDatabase {
    private final ProductEntityRepository repository;
    private final ProductMapper mapper;

    @Override
    @Transactional
    public Product save(Product product) {
        ProductEntity entity = mapper.modelToEntity(product);
        entity = repository.save(entity);
        return mapper.entityToModel(entity);
    }

    @Override
    public List<Product> saveAll(List<Product> products) {
        List<ProductEntity> entities = products.stream()
                .map(mapper::modelToEntity)
                .toList();
        entities = repository.saveAll(entities);
        return entities.stream()
                .map(mapper::entityToModel)
                .toList();
    }

    @Override
    public Optional<Product> findById(Long productId) {
        Optional<ProductEntity> entityOptional = repository.findById(productId);
        return entityOptional.map(mapper::entityToModel);
    }

    @Override
    public List<Product> findAllByIds(List<Long> ids) {
        List<ProductEntity> productEntities = repository.findAllById(ids);
        return productEntities.stream()
                .map(mapper::entityToModel)
                .toList();
    }

    @Override
    public PageableContent<Product> findAll(ProductFilter filter, int pageNumber, int pageSize) {
        Specification<ProductEntity> specification = getSpecificationFromFilter(filter);
        Page<ProductEntity> entityPage = repository.findAll(specification, PageRequest.of(pageNumber, pageSize));
        return PageableContent.<Product>builder()
                .totalElements(entityPage.getTotalElements())
                .totalPages(entityPage.getTotalPages())
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .elements(entityPage.get().map(mapper::entityToModel).toList())
                .build();
    }

    @Override
    @Transactional
    public void delete(Product product) {
        ProductEntity entity = mapper.modelToEntity(product);
        repository.delete(entity);
    }
}
