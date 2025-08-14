package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.SongResponseDTO;

public interface IClientSongService{
    PageResponse<SongResponseDTO> getSongsByGenre(Long genreId, int page, int size);
}
