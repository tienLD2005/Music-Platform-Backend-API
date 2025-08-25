package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.TrendingArtistResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IArtistService{
    List<TrendingArtistResponseDTO> getTrendingArtists(int limit);
    PageResponse<TrendingArtistResponseDTO> getAllArtists(Pageable pageable);
}
