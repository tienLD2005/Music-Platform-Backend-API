package com.ra.base_spring_boot.utils.exception;

public class AlreadyPurchasedException extends RuntimeException {
    public AlreadyPurchasedException(String message) {
        super(message);
    }
}
