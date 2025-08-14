package com.ra.base_spring_boot.dto.resp;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AlbumResponse {
    private Long id;
    private String title;
    private String coverImage;
    private String artistName;
    private Long songCount;
    private LocalDateTime releaseDate;
    private  Long totalPlays;
}