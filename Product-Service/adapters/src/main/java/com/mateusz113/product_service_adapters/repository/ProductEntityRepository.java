package com.mateusz113.product_service_adapters.repository;

import com.mateusz113.product_service_adapters.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductEntityRepository extends JpaRepository<ProductEntity, Long> {
    Page<ProductEntity> findByType(String type, Pageable pageable);
}
