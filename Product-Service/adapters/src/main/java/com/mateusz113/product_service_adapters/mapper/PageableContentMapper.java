package com.mateusz113.product_service_adapters.mapper;

import com.mateusz113.product_service_model.content_management.PageableContent;
import com.mateusz113.product_service_model.product.Product;
import com.mateusz113.product_service_model_public.dto.PageableContentDto;
import com.mateusz113.product_service_model_public.dto.ProductDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, CustomizationElementMapper.class, CustomizationOptionMapper.class})
public interface PageableContentMapper {
    PageableContentDto<ProductDto> modelToDto(PageableContent<Product> products);
}
