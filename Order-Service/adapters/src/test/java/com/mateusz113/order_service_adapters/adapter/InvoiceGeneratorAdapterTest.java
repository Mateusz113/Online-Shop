package com.mateusz113.order_service_adapters.adapter;

import com.mateusz113.order_service_adapters.client.InvoiceGeneratorClient;
import com.mateusz113.order_service_adapters.mapper.InvoiceDataMapper;
import com.mateusz113.order_service_core.port.outgoing.InvoiceGenerator;
import com.mateusz113.order_service_model.invoice.InvoiceData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.mateusz113.order_service_adapters.util.CartServiceAdaptersUtil.getInvoiceData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InvoiceGeneratorAdapterTest {
    private InvoiceGeneratorClient client;
    private InvoiceDataMapper invoiceDataMapper;
    private InvoiceGenerator invoiceGenerator;

    @BeforeEach
    void setUp() {
        client = mock(InvoiceGeneratorClient.class);
        invoiceDataMapper = Mappers.getMapper(InvoiceDataMapper.class);
        invoiceGenerator = new InvoiceGeneratorAdapter(client, invoiceDataMapper);
    }

    @Test
    void generateInvoice_GeneratesInvoiceAndReturnsIt() {
        byte[] invoiceGenerated = new byte[0];
        InvoiceData invoiceData = getInvoiceData();
        when(client.generateInvoice(invoiceDataMapper.modelToDto(invoiceData))).thenReturn(invoiceGenerated);

        byte[] result = invoiceGenerator.generateInvoice(getInvoiceData());

        assertEquals(invoiceGenerated, result);
    }
}
