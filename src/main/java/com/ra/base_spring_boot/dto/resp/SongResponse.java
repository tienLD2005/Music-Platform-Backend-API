package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.constants.SongStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongResponse {
    private Long id;
    private String title;
    private LocalTime duration;
    private String artistName;
    private Long artistId;
    private String albumName;
    private Long albumId;
    private String fileUrl;
    private Integer views;
    private LocalDateTime createdAt;
    private SongStatus status;
    private List<String> genres;

    public SongResponse(Long id, String title, LocalTime duration, String artistName, Long artistId, String albumName, Long albumId, String fileUrl, Integer views, LocalDateTime createdAt, SongStatus status) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.artistName = artistName;
        this.artistId = artistId;
        this.albumName = albumName;
        this.albumId = albumId;
        this.fileUrl = fileUrl;
        this.views = views;
        this.createdAt = createdAt;
        this.status = status;
    }
}
