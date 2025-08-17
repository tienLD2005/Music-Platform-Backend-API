package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.dto.req.SongStatisticsFilterRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongStatisticsResponseDTO {
    private SongStatisticsFilterRequestDTO filter;
    private long totalSongs;
    private List<SongStatResponseDto> playCounts;
    private List<SongStatResponseDto> topFavorites;
}
