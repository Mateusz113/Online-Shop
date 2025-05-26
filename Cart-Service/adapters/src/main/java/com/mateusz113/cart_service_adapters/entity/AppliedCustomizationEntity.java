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

import java.util.List;
import java.util.Objects;

@Entity(name = "APPLIED_CUSTOMIZATION")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppliedCustomizationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private Long sourceId;
    private String name;
    private Boolean multipleChoice;
    @ManyToOne
    @JoinColumn
    private CustomizedProductEntity product;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppliedCustomizationOptionEntity> appliedOptions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppliedCustomizationEntity customization)) return false;
        return Objects.equals(getSourceId(), customization.getSourceId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSourceId());
    }
}
