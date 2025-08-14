package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.resp.GenreResponseDTO;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.model.Genre;
import com.ra.base_spring_boot.repository.IGenreRepository;
import com.ra.base_spring_boot.services.IClientGenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientGenreServiceImpl implements IClientGenreService{

    private final IGenreRepository genreRepository;

    @Override
    public PageResponse<GenreResponseDTO> getAllGenres(int page, int size) {
        Page<Genre> genrePage = genreRepository.findAll(PageRequest.of(page - 1, size));

        List<GenreResponseDTO> dtoList = genrePage.getContent()
                .stream()
                .map(genre -> GenreResponseDTO.builder()
                        .id(genre.getId())
                        .name(genre.getGenreName())
                        .build()
                )
                .toList();

        return new PageResponse<>(
                dtoList,
                page,
                genrePage.getTotalPages(),
                genrePage.getTotalElements(),
                size
        );
    }

}
