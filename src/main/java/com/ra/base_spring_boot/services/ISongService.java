package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.FormSong;
import com.ra.base_spring_boot.dto.resp.ResponseSong;
import com.ra.base_spring_boot.model.Song;
import org.springframework.data.domain.Page;


public interface ISongService {
    Page<ResponseSong> getSongsByAlbum(Long albumId, int page, int size);
    ResponseSong addSongToAlbum(FormSong formSong);
}
