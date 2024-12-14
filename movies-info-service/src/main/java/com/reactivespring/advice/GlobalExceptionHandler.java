package com.reactivespring.advice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = WebExchangeBindException.class)
    public ResponseEntity<?> handalBadRequest(WebExchangeBindException exception, ServerWebExchange exchange) {
        // Dynamically get the API path from the request
        String apiPath = exchange.getRequest().getURI().getPath();

        // Get the timestamp of the error
        String timestamp = LocalDateTime.now().toString();

        // Extract error messages from the exception (all field validation errors)
        List<String> message = exception.getBindingResult().getAllErrors().stream()
                .map(err -> err.getDefaultMessage())
                .sorted()
                .toList();

        // Log the error with the exception stack trace
        log.error("Bad request occurred at {} on API path {}: {}", timestamp, apiPath,
                ExceptionUtils.getStackTrace(exception));

        // Build the error response body
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "BAD_REQUEST",
                message,
                apiPath,
                timestamp);

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
