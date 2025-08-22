package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.constants.AlbumStatus;
import com.ra.base_spring_boot.model.constants.AlbumType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumResponse {
    private Long id;
    private String title;
    private LocalDateTime releaseDate;
    private String coverImage;
    private String artistName;
    private Long songCount;
    private  Long totalPlays;
    private AlbumType type;
    private String streamUrls;
    private List<String> downloadUrl;
}