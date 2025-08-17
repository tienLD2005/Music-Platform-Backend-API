package com.ra.base_spring_boot.exception;

public class AuditLogException extends RuntimeException {
    public AuditLogException(String message) {
        super(message);
    }

    public AuditLogException(String message, Throwable cause) {
        super(message, cause);
    }
}
