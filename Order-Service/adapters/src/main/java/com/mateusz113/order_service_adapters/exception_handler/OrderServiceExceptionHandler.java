package com.mateusz113.order_service_adapters.exception_handler;

import com.mateusz113.order_service_model.exception.WebException;
import com.mateusz113.order_service_model_public.error.ErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class OrderServiceExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(exception = WebException.class)
    public ResponseEntity<Object> handleWebException(WebException exception, WebRequest request) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .message(exception.getMessage())
                .statusCode(exception.getStatusCode())
                .errorTime(exception.getErrorTime())
                .build();
        return handleExceptionInternal(exception, errorMessage, new HttpHeaders(), HttpStatusCode.valueOf(exception.getStatusCode()), request);
    }
}
