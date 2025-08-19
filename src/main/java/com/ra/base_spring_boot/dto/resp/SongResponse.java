package com.ra.base_spring_boot.dto.resp;

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
    private String status;
    private List<String> genres;

}
