package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.FormSong;
import com.ra.base_spring_boot.dto.resp.ResponseSong;
import com.ra.base_spring_boot.model.Song;
import org.springframework.data.domain.Page;


public interface IAlbumService {
    Page<ResponseSong> getSongsByAlbum(Long albumId, int page, int size, String sortBy, String direction);
    ResponseSong addSongToAlbum(Long albumId, FormSong request, String username);
    void deleteSongFromAlbum(Long albumId, Long songId, String name);
}
