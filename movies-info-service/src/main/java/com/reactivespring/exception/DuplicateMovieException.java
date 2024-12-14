package com.reactivespring.exception;

import jakarta.validation.constraints.NotBlank;

public class DuplicateMovieException extends RuntimeException {
    public DuplicateMovieException(@NotBlank(message = "movieInfo.name must be present") String s, Throwable throwable) {
        super(s, throwable);
    }
}
