package com.mateusz113.order_service_adapters.adapter;

import com.mateusz113.order_service_core.port.outgoing.EmailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.Clock;

import static com.mateusz113.order_service_adapters.util.CartServiceAdaptersUtil.getClock;
import static org.mockito.Mockito.mock;

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
    void sendEmail_MessagingExceptionIsThrownByMimeMessageHelper_ThrowsEmailMessagingException() {
        // NIE WIEM JAK NAPISAĆ TESTY DO TEGO
    }

    @Test
    void sendEmail_SendEmail() {
        // NIE WIEM JAK NAPISAĆ TESTY DO TEGO
    }

    @Test
    void sendEmailWithInvoice_MessagingExceptionIsThrownByMimeMessageHelper_ThrowsEmailMessagingException() {
        // NIE WIEM JAK NAPISAĆ TESTY DO TEGO
    }

    @Test
    void sendEmailWithInvoice_SendEmailWithInvoice() {
        // NIE WIEM JAK NAPISAĆ TESTY DO TEGO
    }
}
