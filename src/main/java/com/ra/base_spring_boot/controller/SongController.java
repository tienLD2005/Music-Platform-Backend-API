package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.FormSong;
import com.ra.base_spring_boot.services.ISongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/songs")
@RequiredArgsConstructor
public class SongController {

    private final ISongService songService;

    @GetMapping("album/{albumId}")
    public ResponseEntity<?> getSongsByAlbum(@PathVariable Long albumId,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(songService.getSongsByAlbum(albumId, page - 1, size))
                        .build()
        );
    }

    @PostMapping("album")
    public ResponseEntity<?> addSongToAlbum(@ModelAttribute @Valid FormSong request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.CREATED)
                        .code(HttpStatus.CREATED.value())
                        .data(songService.addSongToAlbum(request))
                        .build()
        );
    }
}
