package com.mateusz113.order_service_core.port.outgoing;

public interface OrderServiceLogger {
    void info(String message);

    void warn(String message);

    void error(String message);
}
