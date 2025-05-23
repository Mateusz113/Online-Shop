package com.mateusz113.order_service_adapters.entity;

import com.mateusz113.order_service_model.order.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Entity(name = "order_entity")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private Long cartId;
    private Long clientId;
    private String clientComment;
    private OffsetDateTime placementTime;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private byte[] invoice;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductEntity> products;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OrderEntity orderEntity)) {
            return false;
        }
        return id != null && id.equals(orderEntity.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
