package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.resp.TrendingArtistResponseDTO;

import java.util.List;

public interface ArtistService {
    List<TrendingArtistResponseDTO> getTrendingArtists(int limit);
}
