package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.resp.GenreTrendingDTO;
import com.ra.base_spring_boot.repository.IGenreRepository;
import com.ra.base_spring_boot.services.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final IGenreRepository genreRepository;

    @Override
    public List<GenreTrendingDTO> getTrendingGenres(String period, int limit) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = switch (period.toLowerCase()) {
            case "week" -> endDate.minusWeeks(1);
            case "month" -> endDate.minusMonths(1);
            default -> throw new IllegalArgumentException("Invalid period: " + period);
        };

        return genreRepository.findTopGenres(startDate, endDate, PageRequest.of(0, limit));
    }
}