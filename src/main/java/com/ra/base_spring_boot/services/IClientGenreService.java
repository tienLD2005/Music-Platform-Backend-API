package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.resp.GenreResponseDTO;
import com.ra.base_spring_boot.dto.resp.PageResponse;

public interface IClientGenreService{
    PageResponse<GenreResponseDTO> getAllGenres(int page, int size);
}
