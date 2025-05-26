package com.mateusz113.cart_service_adapters.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity(name = "CUSTOMIZED_PRODUCT")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomizedProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private Long sourceId;
    private String name;
    private String brand;
    private BigDecimal price;
    private Integer quantity;
    @ManyToOne
    @JoinColumn
    private CartEntity cart;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppliedCustomizationEntity> appliedCustomizations;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomizedProductEntity customizedProduct)) return false;
        return Objects.equals(getSourceId(), customizedProduct.getSourceId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSourceId());
    }
}
