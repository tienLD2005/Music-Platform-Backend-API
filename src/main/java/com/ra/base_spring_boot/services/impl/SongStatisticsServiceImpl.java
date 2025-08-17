package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.SongStatisticsFilterRequestDTO;
import com.ra.base_spring_boot.dto.resp.SongStatResponseDto;
import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.services.ISongStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SongStatisticsServiceImpl implements ISongStatisticsService {
    private final ISongRepository songRepository;

    @Override
    public Map<String, Object> getSongStatistics(SongStatisticsFilterRequestDTO filter) {
        Map<String, Object> result = new HashMap<>();

        long totalSongs = songRepository.countTotalSongs();
        result.put("totalSongs", totalSongs);

        List<SongStatResponseDto> playCounts = songRepository
                .getPlayCountBySong(filter.getArtistId(), filter.getGenreId(), filter.getAlbumId(), filter.getSortBy())
                .stream()
                .map(row -> new SongStatResponseDto(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        ((Number) row[2]).longValue()
                ))
                .toList();
        result.put("playCounts", playCounts);

        List<SongStatResponseDto> topFavorites = songRepository
                .getTopFavoriteSongs()
                .stream()
                .map(row -> new SongStatResponseDto(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        ((Number) row[2]).longValue()
                ))
                .toList();
        result.put("topFavorites", topFavorites);

        return result;
    }

}
