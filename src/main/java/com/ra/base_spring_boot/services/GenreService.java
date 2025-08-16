package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.resp.GenreRequestDTO;
import com.ra.base_spring_boot.dto.resp.GenreResponseDTO;
import com.ra.base_spring_boot.dto.resp.GenreTrendingDTO;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.model.Genre;
import org.hibernate.query.Page;

import java.util.List;

public interface GenreService {
    List<GenreTrendingDTO> getTrendingGenres(String period, int limit);

    PageResponse<GenreResponseDTO> getGenres(String keyword, int page, int size, String sortBy, String direction);

    GenreResponseDTO createGenre(GenreRequestDTO request);
    GenreResponseDTO updateGenre(Long id, GenreRequestDTO request);
    void deleteGenre(Long id);
}
