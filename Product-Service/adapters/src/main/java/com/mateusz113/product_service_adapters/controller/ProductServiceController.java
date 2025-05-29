package com.mateusz113.product_service_adapters.controller;

import com.mateusz113.product_service_adapters.mapper.PageableContentMapper;
import com.mateusz113.product_service_adapters.mapper.ProductFilterMapper;
import com.mateusz113.product_service_adapters.mapper.ProductMapper;
import com.mateusz113.product_service_core.ports.incoming.AddNewProducts;
import com.mateusz113.product_service_core.ports.incoming.CheckProductsStock;
import com.mateusz113.product_service_core.ports.incoming.DeleteProduct;
import com.mateusz113.product_service_core.ports.incoming.GetDetailedProduct;
import com.mateusz113.product_service_core.ports.incoming.GetProducts;
import com.mateusz113.product_service_core.ports.incoming.UpdateProduct;
import com.mateusz113.product_service_core.ports.incoming.UpdateProductsStock;
import com.mateusz113.product_service_model.content_management.PageableContent;
import com.mateusz113.product_service_model.filter.ProductFilter;
import com.mateusz113.product_service_model.product.Product;
import com.mateusz113.product_service_model_public.commands.FilterProductCommand;
import com.mateusz113.product_service_model_public.commands.UpdateProductsStocksCommand;
import com.mateusz113.product_service_model_public.commands.UpsertProductCommand;
import com.mateusz113.product_service_model_public.dto.DetailedProductDto;
import com.mateusz113.product_service_model_public.dto.PageableContentDto;
import com.mateusz113.product_service_model_public.dto.ProductDto;
import com.mateusz113.product_service_model_public.error.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Product Operations")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductServiceController {
    public final AddNewProducts addNewProducts;
    public final DeleteProduct deleteProduct;
    public final GetDetailedProduct getDetailedProduct;
    public final GetProducts getProducts;
    public final CheckProductsStock checkProductsStock;
    public final UpdateProductsStock updateProductsStock;
    public final UpdateProduct updateProduct;
    public final PageableContentMapper contentMapper;
    public final ProductMapper productMapper;
    public final ProductFilterMapper filterMapper;

    @Operation(summary = "Add new products")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Added new products"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data provided, ranging from missing values to data being incorrect logically.",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<ProductDto> addNewProducts(
            @Parameter(description = "List of new products") @RequestBody List<UpsertProductCommand> upsertProductCommands
    ) {
        List<Product> products = productMapper.commandListToModelList(upsertProductCommands);
        products = addNewProducts.add(products);
        return productMapper.modelListToDtoList(products);
    }

    @Operation(summary = "Get products with filtering")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns paged products"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Logically incorrect filters passed, e.g. price being negative number.",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @GetMapping
    public PageableContentDto<ProductDto> getProducts(
            @Parameter(required = false, description = "Product filters") FilterProductCommand command,
            @Parameter(required = false, description = "Paging details") Pageable pageable) {
        ProductFilter filter = filterMapper.commandToModel(command);
        PageableContent<Product> products = getProducts.getAll(filter, pageable.getPageNumber(), pageable.getPageSize());
        return contentMapper.modelToDto(products);
    }

    @Operation(summary = "Get detailed product data")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product was found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Product ID is null.",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product with given ID does not exist.",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @GetMapping("/{id}")
    public DetailedProductDto getProductById(
            @Parameter(description = "Id of the product to be retrieved") @PathVariable Long id
    ) {
        Product product = getDetailedProduct.getById(id);
        return productMapper.modelToDetailedDto(product);
    }

    @Operation(summary = "Check if the product is available in given quantity")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Required stock are available"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Required stock is not available",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Passed id is not valid",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @GetMapping("/{productId}/available-amount")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void checkProductsAvailableAmounts(
            @Parameter(description = "Id of the product to be checked") @PathVariable Long productId,
            @Parameter(description = "Required minimum product stock") @RequestParam Integer requiredStock
    ) {
        checkProductsStock.checkStock(productId, requiredStock);
    }

    @Operation(summary = "Update product stocks")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "All the sold stocks are available"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Not all the sold quantities are available",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not all the passed ids are viable product ids",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @PatchMapping("/available-amount")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProductsAvailableAmounts(@Parameter(description = "Dto containing map of product ids and sold quantities") @RequestBody UpdateProductsStocksCommand updateProductsStocksCommand) {
        updateProductsStock.updateStock(updateProductsStocksCommand.productsStocksMap());
    }

    @Operation(summary = "Update product data")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Product was updated"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Incorrect new data was passed.",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product with given ID does not exist.",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProduct(
            @Parameter(description = "Id of the product to be updated") @PathVariable Long id,
            @Parameter(description = "New product data") @RequestBody UpsertProductCommand upsertProductCommand
    ) {
        Product newProductData = productMapper.commandToModel(upsertProductCommand);
        updateProduct.update(newProductData, id);
    }

    @Operation(summary = "Get detailed product data")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Product was deleted"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Product ID is null.",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product with given ID does not exist.",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(
            @Parameter(description = "Id of the product to be deleted") @PathVariable Long id
    ) {
        deleteProduct.delete(id);
    }
}
