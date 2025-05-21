package com.mateusz113.order_service_adapters.adapter;

import com.mateusz113.order_service_core.port.outgoing.OrderServiceLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderServiceLoggerAdapter implements OrderServiceLogger {
    @Override
    public void info(String message) {
        log.info(message);
    }

    @Override
    public void warn(String message) {
        log.warn(message);
    }

    @Override
    public void error(String message) {
        log.error(message);
    }
}
