package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.constants.AlbumStatus;
import com.ra.base_spring_boot.model.constants.AlbumType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumResponse {
    private Long id;
    private String title;
    private LocalDateTime releaseDate;
    private String coverImage;
    private AlbumStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AlbumType type;
    private String access;
}
