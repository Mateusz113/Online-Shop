package com.mateusz113.cart_service_adapters.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Entity(name = "CART")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private OffsetDateTime creationDate;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomizedProductEntity> customizedProducts;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CartEntity cart)) {
            return false;
        }
        return id != null && id.equals(cart.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
