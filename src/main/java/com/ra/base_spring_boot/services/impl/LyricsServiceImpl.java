package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.LyricsRequest;
import com.ra.base_spring_boot.dto.resp.LyricsResponseDTO;
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
    public LyricsResponseDTO createLyrics(LyricsRequest request) {
        // 1. Lấy song từ DB
        Song song = songRepository.findById(request.getSongId())
                .orElseThrow(() -> new RuntimeException("Song not found"));

        // 2. Kiểm tra fileUrl của Song
        if (song.getFileUrl() == null || song.getFileUrl().isBlank()) {
            throw new IllegalArgumentException("Bài hát không có file âm thanh (fileUrl)");
        }

        // 3. Gọi Speech-to-Text để lấy lyrics
        String content = speechToTextService.convertAudioToText(song.getFileUrl());

        // 4. Lưu lyrics vào DB
        Lyrics lyrics = Lyrics.builder()
                .song(song)
                .content(content)
                .sourceUrl(song.getFileUrl()) // Sử dụng fileUrl của Song
                .createdAt(LocalDateTime.now())
                .build();

        lyricsRepository.save(lyrics);

        return LyricsMapper.toResponse(lyrics);
    }

    @Override
    public LyricsResponseDTO getLyricsBySong(Long songId) {
        Lyrics lyrics = lyricsRepository.findBySongId(songId);
        if (lyrics == null) {
            throw new RuntimeException("Lyrics not found for songId " + songId);
        }
        return LyricsMapper.toResponse(lyrics);
    }
}