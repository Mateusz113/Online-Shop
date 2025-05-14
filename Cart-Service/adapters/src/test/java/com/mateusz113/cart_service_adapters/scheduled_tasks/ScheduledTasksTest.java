package com.mateusz113.cart_service_adapters.scheduled_tasks;

import com.mateusz113.cart_service_adapters.repository.CartRepository;
import com.mateusz113.cart_service_adapters.scheduled_task.ScheduledTasks;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class ScheduledTasksTest {
    @Autowired
    @InjectMocks
    private ScheduledTasks scheduledTasks;

    @MockitoBean
    private CartRepository cartRepository;

    @Test
    public void removeEmptyCarts_DeletesEmptyCartsRegularly() {
        //Test delay is 500 ms
        await().atMost(Duration.ofSeconds(1)).untilAsserted(() -> {
            verify(cartRepository, times(2)).deleteAllByCustomizedProductsEmpty();
        });
    }
}
