package com.mateusz113.cart_service_adapters.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Entity(name = "APPLIED_CUSTOMIZATION_OPTION")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppliedCustomizationOptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private Long sourceId;
    private String name;
    private BigDecimal priceDifference;
    @ManyToOne
    @JoinColumn
    private AppliedCustomizationEntity customization;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppliedCustomizationOptionEntity customizationOption)) return false;
        return Objects.equals(getSourceId(), customizationOption.getSourceId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSourceId());
    }
}
