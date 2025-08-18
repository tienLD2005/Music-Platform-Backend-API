package com.ra.base_spring_boot.exception;

public class AlbumNotFoundException extends RuntimeException {
    public AlbumNotFoundException(String message) {
        super(message);
    }

    public AlbumNotFoundException(Long albumId) {
        super("Không tìm thấy album với ID: " + albumId);
    }
}
