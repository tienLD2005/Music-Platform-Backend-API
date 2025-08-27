package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.repository.IArtistRepository;
import com.ra.base_spring_boot.services.IArtistStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ArtistStatisticsServiceImpl implements IArtistStatisticsService {
    private final IArtistRepository artistRepository;

    @Override
    public Map<String, Object> getArtistStatistics() {
        Map<String, Object> result = new HashMap<>();

        result.put("totalArtists", artistRepository.countArtists());

        Map<String, Object> mostAlbums = artistRepository.findArtistWithMostAlbums(PageRequest.of(0, 1))
                .stream()
                .map(r -> Map.of(
                        "id", r[0],
                        "name", r[1],
                        "albums", ((Number) r[2]).longValue()
                ))
                .findFirst()
                .orElse(null);
        result.put("mostAlbumsArtist", mostAlbums);

        Map<String, Object> mostSongs = artistRepository.findArtistWithMostSongs(PageRequest.of(0, 1))
                .stream()
                .map(r -> Map.of(
                        "id", r[0],
                        "name", r[1],
                        "songs", ((Number) r[2]).longValue()
                ))
                .findFirst()
                .orElse(null);
        result.put("mostSongsArtist", mostSongs);

        List<Map<String, Object>> topPlays = artistRepository.findTopArtistsByPlays(PageRequest.of(0, 5))
                .stream()
                .map(r -> Map.of(
                        "id", r[0],
                        "name", r[1],
                        "plays", ((Number) r[2]).longValue()
                ))
                .toList();
        result.put("topPlayedArtists", topPlays);

        return result;
    }
}
