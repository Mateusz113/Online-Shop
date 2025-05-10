package com.mateusz113.cart_service_adapters.repository;

import com.mateusz113.cart_service_adapters.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {
}
