package com.ra.base_spring_boot.exception;

public class AlbumDeleteException extends RuntimeException {
    public AlbumDeleteException(String message) {
        super(message);
    }

    public AlbumDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
