package com.mateusz113.order_service_adapters.client;

import com.mateusz113.order_service_adapters.config.FeignConfig;
import com.mateusz113.order_service_adapters.config.InvoiceGeneratorClientConfig;
import com.mateusz113.order_service_adapters.fallback.InvoiceGeneratorClientFallbackFactory;
import com.mateusz113.order_service_model_public.dto.InvoiceDataDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "invoice-generator-client",
        url = "${spring.cloud.openfeign.client.config.invoice-generator-client.url}",
        configuration = {FeignConfig.class, InvoiceGeneratorClientConfig.class},
        fallbackFactory = InvoiceGeneratorClientFallbackFactory.class
)
public interface InvoiceGeneratorClient {
    @PostMapping
    byte[] generateInvoice(@RequestBody InvoiceDataDto invoiceData);
}
