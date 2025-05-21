package com.mateusz113.order_service_adapters.adapter;

import com.mateusz113.order_service_core.port.outgoing.EmailSender;
import com.mateusz113.order_service_model.email.EmailData;
import com.mateusz113.order_service_model.exception.EmailMessagingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSenderAdapter implements EmailSender {
    private final Clock clock;
    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(EmailData emailData) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            updateHelperWithEmailData(emailData, helper);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error("Error occurred while trying to send an email message: {}", e.getMessage());
            throw new EmailMessagingException("Error occurred while trying to send an email message.", OffsetDateTime.now(clock));
        }
    }

    @Override
    public void sendEmailWithInvoice(EmailData emailData, byte[] invoice, String attachmentName) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            updateHelperWithEmailData(emailData, helper);
            ByteArrayDataSource invoiceData = new ByteArrayDataSource(invoice, "application/pdf");
            helper.addAttachment(attachmentName, invoiceData);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error("Error occurred while trying to send an email message with invoice: {}", e.getMessage());
            throw new EmailMessagingException("Error occurred while trying to send an email message with invoice.", OffsetDateTime.now(clock));
        }
    }

    private void updateHelperWithEmailData(EmailData emailData, MimeMessageHelper helper) throws MessagingException {
        helper.setTo(emailData.recipientEmail());
        helper.setSubject(emailData.subject());
        helper.setText(emailData.message());
    }
}
