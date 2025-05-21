package com.mateusz113.order_service_adapters.adapter;

import com.mateusz113.order_service_adapters.client.InvoiceGeneratorClient;
import com.mateusz113.order_service_adapters.mapper.InvoiceDataMapper;
import com.mateusz113.order_service_core.port.outgoing.InvoiceGenerator;
import com.mateusz113.order_service_model.invoice.InvoiceData;
import com.mateusz113.order_service_model_public.dto.InvoiceDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvoiceGeneratorAdapter implements InvoiceGenerator {
    private final InvoiceGeneratorClient invoiceClient;
    private final InvoiceDataMapper invoiceDataMapper;

    @Override
    public byte[] generateInvoice(InvoiceData invoiceData) {
        InvoiceDataDto dataDto = invoiceDataMapper.modelToDto(invoiceData);
        return invoiceClient.generateInvoice(dataDto);
    }
}
