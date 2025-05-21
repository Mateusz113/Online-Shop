package com.mateusz113.order_service_model.order;

import com.mateusz113.order_service_model.product.Product;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
@Data
public class Order {
    private Long id;
    private Long cartId;
    private Long clientId;
    private String clientComment;
    private OffsetDateTime placementTime;
    private OrderStatus orderStatus;
    private byte[] invoice;
    private List<Product> products;
}
