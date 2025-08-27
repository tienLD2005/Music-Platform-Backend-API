package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.resp.SongResponse;
import com.ra.base_spring_boot.model.Song;

import java.util.List;

public interface RecommendationService {
    List<SongResponse> recommendSongs(Long userId, int limit);
}
