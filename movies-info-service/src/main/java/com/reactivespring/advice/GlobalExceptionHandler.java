package com.reactivespring.advice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import lombok.extern.slf4j.Slf4j;

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
        String message = exception.getBindingResult().getAllErrors().stream()
                .map(err -> err.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining(", "));

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

    // Define the error response structure
    public static class ErrorResponse {
        private int status;
        private String errorCode;
        private String message;
        private String apiPath;
        private String timestamp;

        public ErrorResponse(int status, String errorCode, String message, String apiPath, String timestamp) {
            this.status = status;
            this.errorCode = errorCode;
            this.message = message;
            this.apiPath = apiPath;
            this.timestamp = timestamp;
        }

        // Getters and Setters
        public int getStatus() {
            return status;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public String getMessage() {
            return message;
        }

        public String getApiPath() {
            return apiPath;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }

}
