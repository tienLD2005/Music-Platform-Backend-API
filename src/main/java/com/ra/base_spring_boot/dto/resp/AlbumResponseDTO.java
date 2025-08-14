package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.constants.AlbumType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumResponseDTO {
    private Long id;
    private String title;
    private LocalDateTime releaseDate;
    private String coverImage;
    private AlbumType type;
    private Long songCount;
}
