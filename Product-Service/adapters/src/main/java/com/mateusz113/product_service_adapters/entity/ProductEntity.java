package com.mateusz113.product_service_adapters.entity;

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

import java.math.BigDecimal;
import java.util.List;

@Entity(name = "PRODUCT")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private String name;
    private String brand;
    private BigDecimal price;
    private String type;
    private Integer availableAmount;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDetailEntity> details;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomizationElementEntity> customizations;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ProductEntity productEntity)) {
            return false;
        }
        return id != null && id.equals(productEntity.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
