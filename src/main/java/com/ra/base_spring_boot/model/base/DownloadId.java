package com.ra.base_spring_boot.model.base;

import java.io.Serializable;
import java.util.Objects;

public class DownloadId implements Serializable {
    private Long user;
    private Long song;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DownloadId that)) return false;
        return Objects.equals(user, that.user) && Objects.equals(song, that.song);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, song);
    }
}

