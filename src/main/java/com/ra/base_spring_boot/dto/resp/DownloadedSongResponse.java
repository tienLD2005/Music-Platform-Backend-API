package com.ra.base_spring_boot.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DownloadedSongResponse {
    private Long songId;
    private String songTitle;
    private String artistName;
    private String albumTitle;
    private String filePath;
    private LocalDateTime addedAt;
    private String duration;
    private String coverImage;
}
