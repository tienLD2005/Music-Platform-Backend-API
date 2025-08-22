package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.LyricsRequest;
import com.ra.base_spring_boot.dto.resp.LyricsResponseDTO;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.mapper.LyricsMapper;
import com.ra.base_spring_boot.model.Lyrics;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.repository.ILyricsRepository;
import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.services.ILyricsService;
import com.ra.base_spring_boot.services.speechToText.SpeechToTextService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LyricsServiceImpl implements ILyricsService {

    private final ILyricsRepository lyricsRepository;
    private final ISongRepository songRepository;
    private final SpeechToTextService speechToTextService;

    @Override
    public LyricsResponseDTO createLyrics(Long songId) {
        if (songId == null || songId <= 0) {
            throw new HttpBadRequest("songId illegal");
        }

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new HttpNotFound("Song not found"));

        if (song.getFileUrl() == null || song.getFileUrl().isBlank()) {
            throw new HttpBadRequest("Song doesn't have file url");
        }

        String content = speechToTextService.convertAudioToText(song.getFileUrl());

        Lyrics lyrics = Lyrics.builder()
                .song(song)
                .content(content)
                .sourceUrl(song.getFileUrl())
                .createdAt(LocalDateTime.now())
                .build();

        lyricsRepository.save(lyrics);

        return LyricsMapper.toResponse(lyrics);
    }

    @Override
    public LyricsResponseDTO getLyricsBySong(Long songId) {
        Lyrics lyrics = lyricsRepository.findBySongId(songId);
        if (lyrics == null) {
            throw new HttpNotFound("Lyrics not found for songId " + songId);
        }
        return LyricsMapper.toResponse(lyrics);
    }
}