// Thêm vào AlbumAdminResponse.java
package com.ra.base_spring_boot.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ra.base_spring_boot.model.constants.AlbumStatus;
import com.ra.base_spring_boot.model.constants.AlbumType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumAdminResponse {
    private Long id;
    private String title;
    private String artistName;
    private String artistEmail;
    private AlbumType type;
    private AlbumStatus status;
    private String coverImage;
    private Long songCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime releaseDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private String statusDisplay;
    private Integer totalViews;
    private Integer totalDownloads;
}
