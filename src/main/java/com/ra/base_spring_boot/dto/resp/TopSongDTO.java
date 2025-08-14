package com.ra.base_spring_boot.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class TopSongDTO {
    private Long id;
    private String title;
    private LocalTime duration;
    private String fileUrl;
    private Integer views;
    private Long totalDownloads;
}
