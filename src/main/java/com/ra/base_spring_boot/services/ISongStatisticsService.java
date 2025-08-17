package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.SongStatisticsFilterRequestDTO;
import com.ra.base_spring_boot.dto.resp.SongStatisticsResponseDTO;


public interface ISongStatisticsService {
    SongStatisticsResponseDTO getSongStatistics(SongStatisticsFilterRequestDTO filter);
}
