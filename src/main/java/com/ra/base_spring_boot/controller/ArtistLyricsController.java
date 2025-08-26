package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.LyricsResponseDTO;
import com.ra.base_spring_boot.services.ILyricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/artist/lyrics")
@RequiredArgsConstructor
public class ArtistLyricsController {

    private final ILyricsService lyricsService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<LyricsResponseDTO>> createLyrics(@RequestParam Long songId) {
        return ResponseEntity.ok(
                ResponseWrapper.<LyricsResponseDTO>builder()
                        .status(HttpStatus.CREATED)
                        .code(HttpStatus.CREATED.value())
                        .data(lyricsService.createLyrics(songId))
                        .build()
        );
    }

    @GetMapping(value = "/song/{songId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<LyricsResponseDTO>> getLyricsBySong(@PathVariable Long songId) {
        return ResponseEntity.ok(
                ResponseWrapper.<LyricsResponseDTO>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(lyricsService.getLyricsBySong(songId))
                        .build()
        );
    }
}