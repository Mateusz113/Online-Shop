package com.mateusz113.product_service_adapters.exception_handler;

import com.mateusz113.product_service_model.exception.WebException;
import com.mateusz113.product_service_model_public.error.ErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ProductServiceExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(exception = WebException.class)
    public ResponseEntity<Object> handleWebException(WebException ex, WebRequest request) {
        ErrorMessage body = ErrorMessage.builder()
                .message(ex.getMessage())
                .statusCode(ex.getStatusCode())
                .errorTime(ex.getErrorTime())
                .build();
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.valueOf(ex.getStatusCode()), request);
    }
}
