package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.req.LyricsRequest;
import com.ra.base_spring_boot.dto.resp.LyricsResponseDTO;
import com.ra.base_spring_boot.services.ILyricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lyrics")
@RequiredArgsConstructor
public class LyricsController {

    private final ILyricsService lyricsService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE) // Thêm produces để chỉ rõ JSON
    public ResponseEntity<LyricsResponseDTO> createLyrics(@RequestParam Long songId) {
        if (songId == null || songId <= 0) {
            throw new IllegalArgumentException("songId không hợp lệ");
        }

        LyricsRequest request = LyricsRequest.builder()
                .songId(songId)
                .build();

        return ResponseEntity.ok(lyricsService.createLyrics(request));
    }

    @GetMapping(value = "/song/{songId}", produces = MediaType.APPLICATION_JSON_VALUE) // Thêm produces nếu cần
    public ResponseEntity<LyricsResponseDTO> getLyricsBySong(@PathVariable Long songId) {
        return ResponseEntity.ok(lyricsService.getLyricsBySong(songId));
    }
}