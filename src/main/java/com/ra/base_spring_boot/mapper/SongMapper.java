package com.ra.base_spring_boot.mapper;

import com.ra.base_spring_boot.dto.resp.SongResponse;
import com.ra.base_spring_boot.dto.resp.SongResponseDTO;
import com.ra.base_spring_boot.model.Genre;
import com.ra.base_spring_boot.model.Song;

import java.util.stream.Collectors;

public class SongMapper {
    public static SongResponse toResponse(Song song) {
        return SongResponse.builder()
                .id(song.getId())
                .title(song.getTitle())
                .duration(song.getDuration())
                .artistId(song.getArtist() != null ? song.getArtist().getId() : null)
                .artistName(song.getArtist() != null
                        ? song.getArtist().getFirstName() + " " + song.getArtist().getLastName()
                        : null)
                .albumId(song.getAlbum() != null ? song.getAlbum().getId() : null)
                .albumName(song.getAlbum() != null ? song.getAlbum().getTitle() : null)
                .fileUrl(song.getFileUrl())
                .views(song.getViews())
                .createdAt(song.getCreatedAt())
                .genres(song.getGenres() != null
                        ? song.getGenres().stream().map(Genre::getGenreName).collect(Collectors.toList())
                        : null)
                .build();
    }

    public static SongResponseDTO mapToDTO(Song song) {
        return SongResponseDTO.builder()
                .id(song.getId())
                .title(song.getTitle())
                .duration(song.getDuration())
                .artistName(song.getArtist() != null ? song.getArtist().getFirstName() + " " + song.getArtist().getLastName() : null)
                .albumName(song.getAlbum() != null ? song.getAlbum().getTitle() : null)
                .fileUrl(song.getFileUrl())
                .views(song.getViews())
                .build();
    }
}
