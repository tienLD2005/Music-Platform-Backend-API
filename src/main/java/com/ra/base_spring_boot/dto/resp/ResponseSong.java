package com.ra.base_spring_boot.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseSong {
    private Long id;
    private String title;
    private LocalTime duration;
    private String fileUrl;
    private Integer views;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ResponseUser artist;
    private ResponseAlbum album;
    private Set<ResponseGenre> genres;
}
