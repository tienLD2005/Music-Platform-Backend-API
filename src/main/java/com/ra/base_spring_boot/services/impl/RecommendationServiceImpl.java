package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.resp.SongResponse;
import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.dto.resp.GenreStatDTO;
import com.ra.base_spring_boot.services.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final ISongRepository songRepo;

    @Override
    public List<SongResponse> recommendSongs(Long userId, int limit) {
        List<GenreStatDTO> stats = songRepo.countGenresByUser(userId);
        if (stats.isEmpty()) {
            return List.of();
        }

        List<Long> topGenreIds = stats.stream()
                .map(GenreStatDTO::getGenreId)
                .limit(3)
                .toList();

        List<Long> listenedSongIds = songRepo.findListenedSongIds(userId);
        if (listenedSongIds.isEmpty()) {
            listenedSongIds = List.of(-1L);
        }

        Page<SongResponse> page = songRepo.findRecommendedSongs(
                topGenreIds,
                listenedSongIds,
                PageRequest.of(0, limit)
        );

        page.forEach(song -> {
            List<String> genres = songRepo.findGenresBySongId(song.getId());
            song.setGenres(genres);
        });

        return page.getContent();
    }

}
