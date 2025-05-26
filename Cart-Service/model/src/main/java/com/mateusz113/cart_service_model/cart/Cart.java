package com.mateusz113.cart_service_model.cart;

import com.mateusz113.cart_service_model.product.CustomizedProduct;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class Cart {
    private Long id;
    private OffsetDateTime creationDate;
    private List<CustomizedProduct> customizedProducts;
}
