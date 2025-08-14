package com.ra.base_spring_boot.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistResp {
    private Long id;
    private String name;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PlaylistResp(Long id, String name, Boolean isPublic, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.isPublic = isPublic;
        this.createdAt = createdAt;
    }
}
