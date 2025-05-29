package com.mateusz113.product_service_adapters.entity;

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

@Entity(name = "CUSTOMIZATION_ELEMENT")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomizationElementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private String name;
    private Boolean multipleChoice;
    @ManyToOne
    @JoinColumn
    private ProductEntity product;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomizationOptionEntity> options;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CustomizationElementEntity customizationElementEntity)) {
            return false;
        }
        return id != null && id.equals(customizationElementEntity.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
