package com.reactivespring.exception;

public class UpdateMovieException extends RuntimeException {
    public UpdateMovieException(String errorUpdatingMovieInfo, Throwable throwable) {
        super(errorUpdatingMovieInfo, throwable);
    }
}
