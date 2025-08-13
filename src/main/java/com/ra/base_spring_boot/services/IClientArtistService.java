package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.resp.ArtistResponseDTO;
import com.ra.base_spring_boot.dto.resp.PageResponse;

public interface IClientArtistService{
    PageResponse<ArtistResponseDTO> getTrendingArtists(int page, int size);
    PageResponse<ArtistResponseDTO> getAllArtists(int page, int size);
}
