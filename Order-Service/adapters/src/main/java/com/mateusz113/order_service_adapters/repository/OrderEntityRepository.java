package com.mateusz113.order_service_adapters.repository;

import com.mateusz113.order_service_adapters.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderEntityRepository extends JpaRepository<OrderEntity, Long> {
    Page<OrderEntity> findAllByClientId(Long clientId, Pageable pageable);
}
