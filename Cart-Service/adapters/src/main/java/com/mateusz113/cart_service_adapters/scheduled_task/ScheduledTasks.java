package com.mateusz113.cart_service_adapters.scheduled_task;

import com.mateusz113.cart_service_adapters.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private final CartRepository cartRepository;

    @Scheduled(fixedDelayString = "${cart.clean.interval}")
    @Transactional
    public void removeEmptyCarts() {
        log.info("Deleting all empty carts");
        cartRepository.deleteAllByCustomizedProductsEmpty();
    }
}
