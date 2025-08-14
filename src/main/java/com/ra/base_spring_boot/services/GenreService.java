package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.resp.GenreTrendingDTO;

import java.util.List;

public interface GenreService {
    List<GenreTrendingDTO> getTrendingGenres(String period, int limit);
}
