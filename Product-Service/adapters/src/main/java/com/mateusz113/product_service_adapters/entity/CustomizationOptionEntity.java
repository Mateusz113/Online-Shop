package com.mateusz113.product_service_adapters.entity;

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

@Entity(name = "CUSTOMIZATION_OPTION")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomizationOptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Boolean defaultOption;
    private BigDecimal priceDifference;
    @ManyToOne
    @JoinColumn
    private CustomizationElementEntity customizationElement;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CustomizationOptionEntity option)) {
            return false;
        }
        return id != null && id.equals(option.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
