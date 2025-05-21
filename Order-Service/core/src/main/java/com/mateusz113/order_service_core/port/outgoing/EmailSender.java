package com.mateusz113.order_service_core.port.outgoing;

import com.mateusz113.order_service_model.email.EmailData;

public interface EmailSender {
    void sendEmail(EmailData emailData);

    void sendEmailWithInvoice(EmailData emailData, byte[] invoice, String attachmentName);
}
