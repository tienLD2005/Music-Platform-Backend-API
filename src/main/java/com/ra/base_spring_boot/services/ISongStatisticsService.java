package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.SongStatisticsFilterRequestDTO;

import java.util.Map;

public interface ISongStatisticsService {
    Map<String, Object> getSongStatistics(SongStatisticsFilterRequestDTO filter);
}
