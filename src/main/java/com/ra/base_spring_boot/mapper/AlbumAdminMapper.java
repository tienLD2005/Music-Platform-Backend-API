package com.ra.base_spring_boot.mapper;

import com.ra.base_spring_boot.dto.resp.AlbumAdminResponse;
import com.ra.base_spring_boot.dto.resp.AlbumResponse;
import com.ra.base_spring_boot.model.Album;
import com.ra.base_spring_boot.model.constants.AlbumStatus;

public class AlbumAdminMapper {

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

    public static AlbumAdminResponse toAlbumAdminResponse(Album album, Long songCount) {
        return AlbumAdminResponse.builder()
                .id(album.getId())
                .title(album.getTitle())
                .artistName(album.getArtist() != null
                        ? album.getArtist().getFirstName() + " " + album.getArtist().getLastName()
                        : "Unknown Artist")
                .artistEmail(album.getArtist() != null ? album.getArtist().getEmail() : null)
                .type(album.getType())
                .status(album.getStatus())
                .coverImage(album.getCoverImage())
                .songCount(songCount)
                .releaseDate(album.getReleaseDate())
                .createdAt(album.getCreatedAt())
                .statusDisplay(getStatusDisplay(album.getStatus()))
                .build();
    }

    private static String getStatusDisplay(AlbumStatus status) {
        if (status == null) return "Not yet vetted";
        return switch (status) {
            case PENDING -> "Not yet vetted";
            case ACTIVE -> "Approved";
            case REJECTED -> "Not suitable";
        };
    }
}
