package com.mateusz113.order_service_adapters.adapter;

import com.mateusz113.order_service_core.port.outgoing.EmailSender;
import com.mateusz113.order_service_model.email.EmailData;
import com.mateusz113.order_service_model.exception.EmailMessagingException;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.Clock;

import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getClock;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getDefaultTime;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getEmailData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailSenderAdapterTest {
    private JavaMailSender javaMailSender;
    private EmailSender emailSender;

    @BeforeEach
    void setUp() {
        Clock clock = getClock();
        javaMailSender = mock(JavaMailSender.class);
        emailSender = new EmailSenderAdapter(clock, javaMailSender);
    }

    @Test
    void sendEmail_MessagingExceptionIsThrown_ThrowsEmailMessagingException() {
        EmailData emailData = getEmailData();
        when(javaMailSender.createMimeMessage()).thenAnswer(invocationOnMock -> {
            throw new MessagingException();
        });

        EmailMessagingException exception = assertThrows(EmailMessagingException.class, () -> emailSender.sendEmail(emailData));

        assertEquals("Error occurred while trying to send an email message.", exception.getMessage());
        assertEquals(500, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void sendEmail_SendEmail() {
        EmailData emailData = getEmailData();
        MimeMessage message = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(message);

        emailSender.sendEmail(emailData);

        verify(javaMailSender).send(message);
    }

    @Test
    void sendEmailWithInvoice_MessagingExceptionIsThrown_ThrowsEmailMessagingException() {
        EmailData emailData = getEmailData();
        byte[] invoice = new byte[2];
        String attachmentName = "attachmentName";
        when(javaMailSender.createMimeMessage()).thenAnswer(invocationOnMock -> {
            throw new MessagingException();
        });


        EmailMessagingException exception = assertThrows(EmailMessagingException.class, () -> emailSender.sendEmailWithInvoice(emailData, invoice, attachmentName));

        assertEquals("Error occurred while trying to send an email message with invoice.", exception.getMessage());
        assertEquals(500, exception.getStatusCode());
        assertEquals(getDefaultTime(), exception.getErrorTime());
    }

    @Test
    void sendEmailWithInvoice_SendEmailWithInvoice() {
        EmailData emailData = getEmailData();
        byte[] invoice = new byte[2];
        String attachmentName = "attachmentName";
        MimeMessage message = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(message);

        emailSender.sendEmailWithInvoice(emailData, invoice, attachmentName);

        verify(javaMailSender).send(message);
    }
}
