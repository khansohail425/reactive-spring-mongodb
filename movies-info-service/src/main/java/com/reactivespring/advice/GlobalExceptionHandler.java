package com.reactivespring.advice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> handleWebExchangeBindException(
            WebExchangeBindException exception, ServerWebExchange exchange) {

        // Dynamically get the API path from the request
        String apiPath = exchange.getRequest().getURI().getPath();

        // Get the timestamp of the error in UTC
        String timestamp = Instant.now().toString();

        // Extract error messages from the exception (all field validation errors)
        List<String> errorMessages = exception.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)  // Extract default error message
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());

        // Log the error with the exception stack trace
        log.error("Bad request occurred at {} on API path {}: {}", timestamp, apiPath,
                exception.getMessage(), exception);  // Improved logging format

        // Create an error response
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "BAD_REQUEST",
                errorMessages,
                apiPath,
                timestamp
        );

        // Return the error response with the detailed information
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorResponse {
        private int status;
        private String errorCode;
        private List<String> message;
        private String apiPath;
        private String timestamp;
    }

}
