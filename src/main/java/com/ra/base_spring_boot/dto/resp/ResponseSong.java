package com.ra.base_spring_boot.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseSong {
    private Long id;
    private String artistName;
    private String title;
    private LocalTime duration;
    private Integer views;
    private String fileUrl;
}
