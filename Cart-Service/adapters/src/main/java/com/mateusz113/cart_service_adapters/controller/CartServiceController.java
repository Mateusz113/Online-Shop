package com.mateusz113.cart_service_adapters.controller;

import com.mateusz113.cart_service_adapters.mapper.CartMapper;
import com.mateusz113.cart_service_adapters.mapper.CustomizedProductMapper;
import com.mateusz113.cart_service_core.ports.incoming.AddCart;
import com.mateusz113.cart_service_core.ports.incoming.AddProduct;
import com.mateusz113.cart_service_core.ports.incoming.DeleteCart;
import com.mateusz113.cart_service_core.ports.incoming.DeleteProduct;
import com.mateusz113.cart_service_core.ports.incoming.GetCart;
import com.mateusz113.cart_service_model.cart.Cart;
import com.mateusz113.cart_service_model.product.CustomizedProduct;
import com.mateusz113.cart_service_model_public.command.InsertCartCommand;
import com.mateusz113.cart_service_model_public.command.InsertCustomizedProductCommand;
import com.mateusz113.cart_service_model_public.dto.CartDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Cart Operations")
@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartServiceController {
    private final AddCart addCart;
    private final AddProduct addProduct;
    private final GetCart getCart;
    private final DeleteCart deleteCart;
    private final DeleteProduct deleteProduct;
    private final CartMapper cartMapper;
    private final CustomizedProductMapper productMapper;

    @PostMapping
    public CartDto addCartImpl(@RequestBody InsertCartCommand command) {
        Cart cart = cartMapper.commandToModel(command);
        cart = addCart.add(cart);
        return cartMapper.modelToDto(cart);
    }

    @PostMapping("/{cartId}/customizedProducts")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addProduct(
            @PathVariable Long cartId,
            @RequestBody InsertCustomizedProductCommand command
    ) {
        CustomizedProduct product = productMapper.commandToModel(command);
        addProduct.add(product, cartId);
    }

    @GetMapping("/{cartId}")
    public CartDto getCart(@PathVariable Long cartId) {
        Cart cart = getCart.get(cartId);
        return cartMapper.modelToDto(cart);
    }

    @DeleteMapping("/{cartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCart(@PathVariable Long cartId) {
        deleteCart.deleteCart(cartId);
    }

    @DeleteMapping("/customizedProducts/{productId}")
    public void deleteProduct(@PathVariable Long productId) {
        deleteProduct.deleteProduct(productId);
    }
}
