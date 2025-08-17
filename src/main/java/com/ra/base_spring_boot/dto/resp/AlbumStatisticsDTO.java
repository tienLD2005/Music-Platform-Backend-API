package com.ra.base_spring_boot.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlbumStatisticsDTO {
    private Long albumId;
    private String title;
    private Long count;
}