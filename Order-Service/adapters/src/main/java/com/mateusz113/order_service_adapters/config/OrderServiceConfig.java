package com.mateusz113.order_service_adapters.config;

import com.mateusz113.order_service_core.facade.OrderServiceActionFacade;
import com.mateusz113.order_service_core.facade.OrderServiceEventFacade;
import com.mateusz113.order_service_core.port.incoming.OrderServiceActionPorts;
import com.mateusz113.order_service_core.port.incoming.OrderServiceEventPorts;
import com.mateusz113.order_service_core.port.outgoing.CartServiceCommunicator;
import com.mateusz113.order_service_core.port.outgoing.EmailSender;
import com.mateusz113.order_service_core.port.outgoing.EventSender;
import com.mateusz113.order_service_core.port.outgoing.InvoiceGenerator;
import com.mateusz113.order_service_core.port.outgoing.OrderServiceDatabase;
import com.mateusz113.order_service_core.port.outgoing.OrderServiceLogger;
import com.mateusz113.order_service_core.port.outgoing.ProductServiceCommunicator;
import com.mateusz113.order_service_core.verifier.OrderServiceVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class OrderServiceConfig {
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public OrderServiceVerifier orderServiceVerifier(Clock clock) {
        return new OrderServiceVerifier(clock);
    }

    @Bean
    public OrderServiceEventPorts orderServiceEventPorts(
            CartServiceCommunicator cartServiceCommunicator,
            ProductServiceCommunicator productServiceCommunicator,
            OrderServiceDatabase database,
            OrderServiceVerifier verifier,
            InvoiceGenerator invoiceGenerator,
            EmailSender emailSender,
            OrderServiceLogger logger,
            EventSender eventSender,
            Clock clock
    ) {
        return new OrderServiceEventFacade(
                cartServiceCommunicator,
                productServiceCommunicator,
                database,
                verifier,
                invoiceGenerator,
                emailSender,
                logger,
                eventSender,
                clock
        );
    }

    @Bean
    public OrderServiceActionPorts orderServiceActionPorts(
            CartServiceCommunicator cartServiceCommunicator,
            ProductServiceCommunicator productServiceCommunicator,
            OrderServiceDatabase database,
            OrderServiceVerifier verifier,
            Clock clock
    ) {
        return new OrderServiceActionFacade(cartServiceCommunicator, productServiceCommunicator, database, verifier, clock);
    }
}
