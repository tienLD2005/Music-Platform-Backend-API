package com.ra.base_spring_boot.mapper;

import com.ra.base_spring_boot.dto.resp.DownloadedSongResponse;
import com.ra.base_spring_boot.model.Album;
import com.ra.base_spring_boot.model.Download;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.User;

public class DownloadSongMapper {
    public DownloadedSongResponse mapToDownloadedSongResponse(Download download) {
        Song song = download.getSong();
        Album album = song.getAlbum();
        User artist = song.getArtist();

        return DownloadedSongResponse.builder()
                .songId(song.getId())
                .songTitle(song.getTitle())
                .artistName(artist.getFirstName() + " " + artist.getLastName())
                .albumTitle(album != null ? album.getTitle() : "Single")
                .filePath(download.getFilePath())
                .addedAt(download.getAddedAt())
                .duration(song.getDuration() != null ? song.getDuration().toString() : "Unknown")
                .coverImage(album != null ? album.getCoverImage() : null)
                .build();
    }
}
