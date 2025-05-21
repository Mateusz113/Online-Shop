package com.mateusz113.order_service_core.port.outgoing;

import com.mateusz113.order_service_model.invoice.InvoiceData;

public interface InvoiceGenerator {
    byte[] generateInvoice(InvoiceData invoiceData);
}
