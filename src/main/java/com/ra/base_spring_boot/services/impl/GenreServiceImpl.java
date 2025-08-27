package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.resp.GenreRequestDTO;
import com.ra.base_spring_boot.dto.resp.GenreResponseDTO;
import com.ra.base_spring_boot.dto.resp.GenreTrendingDTO;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Genre;
import com.ra.base_spring_boot.repository.IGenreRepository;
import com.ra.base_spring_boot.services.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final IGenreRepository genreRepository;

    @Override
    public List<GenreTrendingDTO> getTrendingGenres(String period, int limit) {
        if (limit <= 0) {
            throw new HttpBadRequest("Limit must be greater than 0");
        }

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = switch (period.toLowerCase()) {
            case "week" -> endDate.minusWeeks(1);
            case "month" -> endDate.minusMonths(1);
            default -> throw new HttpBadRequest("Invalid period: " + period);
        };

        Pageable pageable = PageRequest.of(0, limit);

        Page<GenreTrendingDTO> pageResult = genreRepository.findTopGenres(startDate, endDate, pageable);

        return pageResult.getContent();
    }

    @Override
    public PageResponse<GenreResponseDTO> getGenres(String keyword, int page, int size, String sortBy, String direction) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));

        List<Genre> genres;
        if ("songCount".equalsIgnoreCase(sortBy)) {
            if ("desc".equalsIgnoreCase(direction)) {
                genres = genreRepository.findGenresOrderBySongCountDesc(keyword);
            } else {
                genres = genreRepository.findGenresOrderBySongCountAsc(keyword);
            }
        } else {
            genres = genreRepository.findGenresByName(keyword);
            genres.sort("desc".equalsIgnoreCase(direction)
                    ? Comparator.comparing(Genre::getGenreName).reversed()
                    : Comparator.comparing(Genre::getGenreName));
        }


        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), genres.size());
        List<GenreResponseDTO> content = genres.subList(start, end).stream()
                .map(g -> GenreResponseDTO.builder()
                        .id(g.getId())
                        .name(g.getGenreName())
                        .build())
                .toList();

        return PageResponse.<GenreResponseDTO>builder()
                .content(content)
                .currentPage(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalElements(genres.size())
                .totalPages((int) Math.ceil((double) genres.size() / pageable.getPageSize()))
                .build();
    }

    @Override
    public GenreResponseDTO createGenre(GenreRequestDTO request) {
        Genre genre = Genre.builder()
                .genreName(request.getName())
                .description(request.getDescription())
                .build();
        genreRepository.save(genre);
        return GenreResponseDTO.builder()
                .id(genre.getId())
                .name(genre.getGenreName())
                .build();
    }

    @Override
    public GenreResponseDTO updateGenre(Long id, GenreRequestDTO request) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Genre not found"));
        genre.setGenreName(request.getName());
        genre.setDescription(request.getDescription());
        genreRepository.save(genre);
        return GenreResponseDTO.builder()
                .id(genre.getId())
                .name(genre.getGenreName())
                .build();
    }

    @Override
    public void deleteGenre(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Genre not found"));

        if (genre.getSongs() != null && !genre.getSongs().isEmpty()) {
            throw new HttpNotFound("Cannot delete genre with linked songs");
        }

        genreRepository.delete(genre);
    }
}