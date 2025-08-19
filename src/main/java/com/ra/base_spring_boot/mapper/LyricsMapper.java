package com.ra.base_spring_boot.mapper;

import com.ra.base_spring_boot.dto.resp.LyricsResponseDTO;
import com.ra.base_spring_boot.model.Lyrics;

public class LyricsMapper {
    public static LyricsResponseDTO toResponse(Lyrics lyrics) {
        return LyricsResponseDTO.builder()
                .id(lyrics.getId())
                .content(lyrics.getContent())
                .sourceUrl(lyrics.getSourceUrl())
                .songId(lyrics.getSong().getId())
                .build();
    }
}
