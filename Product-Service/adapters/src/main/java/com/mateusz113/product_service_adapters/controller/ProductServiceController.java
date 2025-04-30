package com.mateusz113.product_service_adapters.controller;

import com.mateusz113.product_service_adapters.mapper.PageableContentMapper;
import com.mateusz113.product_service_adapters.mapper.ProductMapper;
import com.mateusz113.product_service_core.ports.incoming.AddNewProducts;
import com.mateusz113.product_service_core.ports.incoming.DeleteProduct;
import com.mateusz113.product_service_core.ports.incoming.GetProduct;
import com.mateusz113.product_service_core.ports.incoming.GetProducts;
import com.mateusz113.product_service_core.ports.incoming.UpdateProduct;
import com.mateusz113.product_service_model.content_management.PageableContent;
import com.mateusz113.product_service_model.product.Product;
import com.mateusz113.product_service_model_public.commands.UpsertProductCommand;
import com.mateusz113.product_service_model_public.dto.PageableContentDto;
import com.mateusz113.product_service_model_public.dto.ProductDto;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductServiceController {
    public final AddNewProducts addNewProducts;
    public final DeleteProduct deleteProduct;
    public final GetProduct getProduct;
    public final GetProducts getProducts;
    public final UpdateProduct updateProduct;
    public final PageableContentMapper contentMapper;
    public final ProductMapper productMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<ProductDto> addNewProducts(@RequestBody List<UpsertProductCommand> upsertProductCommands) {
        List<Product> products = productMapper.commandListToModelList(upsertProductCommands);
        products = addNewProducts.add(products);
        return productMapper.modelListToDtoList(products);
    }

    @GetMapping
    public PageableContentDto<ProductDto> getProducts(Pageable pageable) {
        PageableContent<Product> products = getProducts.getAll(pageable.getPageNumber(), pageable.getPageSize());
        return contentMapper.modelToDto(products);
    }

    @GetMapping("/type/{type}")
    public PageableContentDto<ProductDto> getProductsByType(@PathVariable String type, Pageable pageable) {
        PageableContent<Product> products = getProducts.getByType(type, pageable.getPageNumber(), pageable.getPageSize());
        return contentMapper.modelToDto(products);
    }

    @GetMapping("/{id}")
    public ProductDto getProductById(@PathVariable Long id) {
        Product product = getProduct.getById(id);
        return productMapper.modelToDto(product);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProduct(@PathVariable Long id, @RequestBody UpsertProductCommand upsertProductCommand) {
        Product newProductData = productMapper.commandToModel(upsertProductCommand);
        updateProduct.update(newProductData, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        deleteProduct.delete(id);
    }
}
