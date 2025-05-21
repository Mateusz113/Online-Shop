package com.mateusz113.order_service_adapters.entity;

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

@Entity(name = "customization_entity")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomizationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private String name;
    private Boolean multipleChoice;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomizationOptionEntity> options;
    @ManyToOne
    @JoinColumn
    private ProductEntity product;
}
