package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.SongStatisticsFilterRequestDTO;
import com.ra.base_spring_boot.dto.resp.SongStatResponseDto;
import com.ra.base_spring_boot.dto.resp.SongStatisticsResponseDTO;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.repository.IAlbumRepository;
import com.ra.base_spring_boot.repository.IGenreRepository;
import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.ISongStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SongStatisticsServiceImpl implements ISongStatisticsService {
    private final ISongRepository songRepository;
    private final IUserRepository userRepository;
    private final IGenreRepository genreRepository;
    private final IAlbumRepository  albumRepository;

    @Override
    public SongStatisticsResponseDTO getSongStatistics(SongStatisticsFilterRequestDTO filter) {
        if (filter.getArtistId() != null && !userRepository.existsById(filter.getArtistId())) {
            throw new HttpNotFound("Artist not found with id " + filter.getArtistId());
        }
        if (filter.getGenreId() != null && !genreRepository.existsById(filter.getGenreId())) {
            throw new HttpNotFound("Genre not found with id " + filter.getGenreId());
        }
        if (filter.getAlbumId() != null && !albumRepository.existsById(filter.getAlbumId())) {
            throw new HttpNotFound("Album not found with id " + filter.getAlbumId());
        }

        long totalSongs = songRepository.countTotalSongs();

        List<SongStatResponseDto> playCounts = songRepository
                .getPlayCountBySong(filter.getArtistId(), filter.getGenreId(), filter.getAlbumId(), filter.getSortBy())
                .stream()
                .map(row -> new SongStatResponseDto(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        ((Number) row[2]).longValue()
                ))
                .toList();

        List<SongStatResponseDto> topFavorites = songRepository
                .getTopFavoriteSongsFiltered(filter.getArtistId(), filter.getGenreId(), filter.getAlbumId())
                .stream()
                .map(row -> new SongStatResponseDto(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        ((Number) row[2]).longValue()
                ))
                .toList();

        return new SongStatisticsResponseDTO(filter, totalSongs, playCounts, topFavorites);
    }

}
