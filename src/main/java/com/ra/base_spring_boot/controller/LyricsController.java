package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.req.LyricsRequest;
import com.ra.base_spring_boot.dto.resp.LyricsResponseDTO;
import com.ra.base_spring_boot.services.ILyricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lyrics")
@RequiredArgsConstructor
public class LyricsController {

    private final ILyricsService lyricsService;

    @PreAuthorize("hasAuthority('ROLE_ARTIST')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LyricsResponseDTO> createLyrics(@RequestParam Long songId) {
        return ResponseEntity.ok(lyricsService.createLyrics(songId));
    }

    @PreAuthorize("hasAuthority('ROLE_ARTIST')")
    @GetMapping(value = "/song/{songId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LyricsResponseDTO> getLyricsBySong(@PathVariable Long songId) {
        return ResponseEntity.ok(lyricsService.getLyricsBySong(songId));
    }
}