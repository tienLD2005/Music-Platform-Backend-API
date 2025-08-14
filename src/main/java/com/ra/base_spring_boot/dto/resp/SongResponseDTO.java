package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.Song;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongResponseDTO {
    private Long id;
    private String title;
    private LocalTime duration;
    private String artistName;
    private String albumName;
    private String fileUrl;
    private Integer views;

}
