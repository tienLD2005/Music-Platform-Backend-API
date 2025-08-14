package com.ra.base_spring_boot.mapper;

import com.ra.base_spring_boot.dto.resp.AlbumResponse;
import com.ra.base_spring_boot.model.Album;

public class AlbumMapper {
    public static AlbumResponse toAlbumResponse(Album album, Long songCount) {
        return AlbumResponse.builder()
                .id(album.getId())
                .title(album.getTitle())
                .coverImage(album.getCoverImage())
                .artistName(album.getArtist() != null
                        ? album.getArtist().getFirstName() + " " + album.getArtist().getLastName()
                        : null)
                .songCount(songCount)
                .releaseDate(album.getReleaseDate())
                .build();
    }
}
