package com.ra.base_spring_boot.dto.resp;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SongStatResponseDto {
    private Long songId;
    private String name;
    private Long count;
}
