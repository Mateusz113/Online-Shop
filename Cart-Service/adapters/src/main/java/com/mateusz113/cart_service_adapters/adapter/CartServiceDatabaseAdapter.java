package com.mateusz113.cart_service_adapters.adapter;

import com.mateusz113.cart_service_adapters.entity.CartEntity;
import com.mateusz113.cart_service_adapters.entity.CustomizedProductEntity;
import com.mateusz113.cart_service_adapters.mapper.CartMapper;
import com.mateusz113.cart_service_adapters.mapper.CustomizedProductMapper;
import com.mateusz113.cart_service_adapters.repository.CartRepository;
import com.mateusz113.cart_service_adapters.repository.CustomizedProductRepository;
import com.mateusz113.cart_service_core.ports.outgoing.CartServiceDatabase;
import com.mateusz113.cart_service_model.cart.Cart;
import com.mateusz113.cart_service_model.product.CustomizedProduct;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class CartServiceDatabaseAdapter implements CartServiceDatabase {
    private final CartRepository cartRepository;
    private final CustomizedProductRepository productRepository;
    private final CartMapper cartMapper;
    private final CustomizedProductMapper productMapper;

    @Override
    public Cart save(Cart cart) {
        CartEntity entity = cartMapper.modelToEntity(cart);
        entity = cartRepository.save(entity);
        return cartMapper.entityToModel(entity);
    }

    @Override
    public Optional<Cart> findCartById(Long id) {
        Optional<CartEntity> entity = cartRepository.findById(id);
        return entity.map(cartMapper::entityToModel);
    }

    @Override
    public Optional<CustomizedProduct> findProductById(Long id) {
        Optional<CustomizedProductEntity> entity = productRepository.findById(id);
        return entity.map(productMapper::entityToModel);
    }

    @Override
    public void delete(Cart cart) {
        CartEntity entity = cartMapper.modelToEntity(cart);
        cartRepository.delete(entity);
    }

    @Override
    public void deleteProduct(CustomizedProduct customizedProduct) {
        CustomizedProductEntity entity = productMapper.modelToEntity(customizedProduct);
        productRepository.delete(entity);
    }
}
