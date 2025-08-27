package com.ra.base_spring_boot.exception;

public class HttpInternalServerError extends RuntimeException {
    public HttpInternalServerError(String message) {
        super(message);
    }
}
