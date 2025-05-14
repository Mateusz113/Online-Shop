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
import com.mateusz113.cart_service_model_public.error.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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

    @Operation(summary = "Add new cart")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully added new cart."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Data provided is not valid, e.g. null values. More detailed information will be in the response body.",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "The source information about the customized product is not present.",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Communication with product service failed.",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Either this or product service is currently unavailable.",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartDto addCart(@RequestBody InsertCartCommand command) {
        log.info("AddCart request received with data: {}", command);
        Cart cart = cartMapper.commandToModel(command);
        cart = addCart.add(cart);
        return cartMapper.modelToDto(cart);
    }

    @Operation(summary = "Add new product to existing cart")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successfully added new product."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Data provided is not valid, e.g. null values. More detailed information will be in the response body.",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "The source information about the customized product or cart with given ID are not present.",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Communication with product service failed.",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Either this or product service is currently unavailable.",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @PatchMapping("/{cartId}/customizedProducts")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addProduct(
            @PathVariable Long cartId,
            @RequestBody InsertCustomizedProductCommand command
    ) {
        log.info("AddProduct request received with cart ID: {}, and data: {}", cartId, command);
        CustomizedProduct product = productMapper.commandToModel(command);
        addProduct.add(product, cartId);
    }

    @Operation(summary = "Get cart")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved cart."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cart with given ID does not exist.",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @GetMapping("/{cartId}")
    public CartDto getCart(@PathVariable Long cartId) {
        log.info("GetCart request received with cartId: {}", cartId);
        Cart cart = getCart.getCart(cartId);
        return cartMapper.modelToDto(cart);
    }

    @Operation(summary = "Delete cart")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successfully deleted cart."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cart with given ID does not exist.",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @DeleteMapping("/{cartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCart(@PathVariable Long cartId) {
        log.info("DeleteCart request received with cartId: {}", cartId);
        deleteCart.deleteCart(cartId);
    }

    @Operation(summary = "Delete product")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successfully deleted product."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product with given ID does not exist.",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @DeleteMapping("/customizedProducts/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long productId) {
        log.info("DeleteProduct request received with productId: {}", productId);
        deleteProduct.deleteProduct(productId);
    }
}
