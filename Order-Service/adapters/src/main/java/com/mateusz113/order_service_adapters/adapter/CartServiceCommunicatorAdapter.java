package com.mateusz113.order_service_adapters.adapter;

import com.mateusz113.order_service_adapters.client.CartServiceClient;
import com.mateusz113.order_service_adapters.mapper.ProductMapper;
import com.mateusz113.order_service_core.port.outgoing.CartServiceCommunicator;
import com.mateusz113.order_service_model.product.Product;
import com.mateusz113.order_service_model_public.dto.CartProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CartServiceCommunicatorAdapter implements CartServiceCommunicator {
    private final CartServiceClient cartClient;
    private final ProductMapper productMapper;

    @Override
    public List<Product> getProductsData(Long cartId) {
        List<CartProductDto> products = cartClient.getCartById(cartId).customizedProducts();
        return products.stream()
                .map(productMapper::dtoToModel)
                .toList();
    }

    @Override
    public void deleteCart(Long cartId) {
        cartClient.deleteCart(cartId);
    }
}
