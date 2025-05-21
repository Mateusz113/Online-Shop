package com.mateusz113.order_service_adapters.mapper;

import com.mateusz113.order_service_model.invoice.InvoiceData;
import com.mateusz113.order_service_model_public.dto.InvoiceDataDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvoiceDataMapper {
    InvoiceDataDto modelToDto(InvoiceData model);
}
