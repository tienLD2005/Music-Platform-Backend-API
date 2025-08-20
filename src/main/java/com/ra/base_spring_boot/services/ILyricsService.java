package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.LyricsRequest;
import com.ra.base_spring_boot.dto.resp.LyricsResponseDTO;

public interface ILyricsService {
    LyricsResponseDTO createLyrics(Long songId);
    LyricsResponseDTO getLyricsBySong(Long songId);
}
