package com.mateusz113.order_service_adapters.fallback;

import com.mateusz113.order_service_adapters.client.InvoiceGeneratorClient;
import com.mateusz113.order_service_model.exception.ServiceCommunicationErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class InvoiceGeneratorClientFallbackFactory implements FallbackFactory<InvoiceGeneratorClient> {
    private final Clock clock;

    @Override
    public InvoiceGeneratorClient create(Throwable cause) {
        return invoiceData -> {
            throw new ServiceCommunicationErrorException("Could not communicate with invoice generator.", OffsetDateTime.now(clock));
        };
    }
}
