package com.mateusz113.product_service_adapters.exception_handler;

import com.mateusz113.product_service_model.exception.WebException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/exception")
public class ExceptionThrowingController {
    @GetMapping("/web_exception")
    public void throwException(@RequestParam String message) {
        throw new WebException(message, 400, OffsetDateTime.now()) {
        };
    }
}
