package com.reactivespring.exception;

import jakarta.validation.constraints.NotBlank;

public class MovieNotFoundException extends RuntimeException {
    public MovieNotFoundException(@NotBlank(message = "movieInfo.name must be present") String message, Throwable throwable) {
        super(message, throwable);
    }
}
