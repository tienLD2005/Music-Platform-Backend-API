package com.ra.base_spring_boot.mapper;

import com.ra.base_spring_boot.dto.resp.DownloadedSongResponse;
import com.ra.base_spring_boot.model.Album;
import com.ra.base_spring_boot.model.Download;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.User;
import org.springframework.stereotype.Component;

@Component
public class DownloadMapper {

    public DownloadedSongResponse mapToDownloadedSongResponse(Download download) {
        if (download == null) {
            throw new IllegalArgumentException("Download không được null");
        }

        Song song = download.getSong();
        if (song == null) {
            throw new IllegalArgumentException("Song trong Download không được null");
        }

        Album album = song.getAlbum();
        User artist = song.getArtist();

        return DownloadedSongResponse.builder()
                .songId(song.getId())
                .songTitle(song.getTitle())
                .artistName(buildArtistName(artist))
                .albumTitle(getAlbumTitle(album))
                .filePath(download.getFilePath())
                .addedAt(download.getAddedAt())
                .duration(getDurationString(song))
                .coverImage(getCoverImage(album))
                .build();
    }

    private String buildArtistName(User artist) {
        if (artist == null) {
            return "Unknown Artist";
        }

        String firstName = artist.getFirstName() != null ? artist.getFirstName() : "";
        String lastName = artist.getLastName() != null ? artist.getLastName() : "";

        return (firstName + " " + lastName).trim();
    }

    private String getAlbumTitle(Album album) {
        return album != null && album.getTitle() != null ? album.getTitle() : "Single";
    }

    private String getDurationString(Song song) {
        if (song.getDuration() != null) {
            return song.getDuration().toString();
        }
        return "Unknown";
    }

    private String getCoverImage(Album album) {
        return album != null ? album.getCoverImage() : null;
    }
}
