package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.constants.AlbumType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseAlbum {
    private Long id;
    private String title;
    private String coverImage;
    private AlbumType type;
}
