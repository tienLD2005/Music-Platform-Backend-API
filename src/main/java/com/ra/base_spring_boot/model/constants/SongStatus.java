package com.ra.base_spring_boot.model.constants;

public enum SongStatus {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private final String display;

    SongStatus(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
