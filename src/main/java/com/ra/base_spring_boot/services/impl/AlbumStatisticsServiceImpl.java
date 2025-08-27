package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.resp.AlbumStatisticsDTO;
import com.ra.base_spring_boot.dto.resp.AlbumStatusDTO;
import com.ra.base_spring_boot.dto.resp.AlbumTrendDTO;
import com.ra.base_spring_boot.model.constants.AlbumStatus;
import com.ra.base_spring_boot.repository.IAlbumRepository;
import com.ra.base_spring_boot.services.IAlbumStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AlbumStatisticsServiceImpl implements IAlbumStatisticsService {
    private final IAlbumRepository  albumRepository;

    @Override
    public Map<String, Object> getAlbumStatistics() {
        Map<String, Object> result = new HashMap<>();

        result.put("totalAlbums", albumRepository.countTotalAlbums());

        List<AlbumStatisticsDTO> mostPlayed = albumRepository.findMostPlayedAlbums().stream()
                .map(r -> new AlbumStatisticsDTO(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        ((Number) r[2]).longValue()
                ))
                .toList();
        result.put("mostPlayedAlbums", mostPlayed);

        List<Map<String, Object>> byArtist = albumRepository.countAlbumsByArtist().stream()
                .map(r -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("artistName", (String) r[0]);
                    m.put("totalAlbums", ((Number) r[1]).longValue());
                    return m;
                })
                .toList();
        result.put("albumsByArtist", byArtist);

        List<AlbumTrendDTO> trend = albumRepository.countAlbumsByYear().stream()
                .map(r -> new AlbumTrendDTO((Integer) r[0], ((Number) r[1]).longValue()))
                .toList();
        result.put("albumTrend", trend);

        List<AlbumStatusDTO> byStatus = albumRepository.countAlbumsByStatus().stream()
                .map(r -> {
                    AlbumStatus status = (AlbumStatus) r[0];
                    String statusName = (status != null) ? status.name() : "UNKNOWN";
                    Long count = (r[1] != null) ? ((Number) r[1]).longValue() : 0L;
                    return new AlbumStatusDTO(statusName, count);
                })
                .toList();
        result.put("albumsByStatus", byStatus);

        return result;
    }
}
