package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.DeleteSongRequest;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.SongResponse;
import com.ra.base_spring_boot.services.ISongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/songs")
@RequiredArgsConstructor
public class AdminSongController {
    private final ISongService songService;

    @GetMapping
    public ResponseEntity<PageResponse<SongResponse>> getAllSongs(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(songService.getAllSongs(keyword, pageable));
    }


    @DeleteMapping("/{songId}")
    public ResponseEntity<ResponseWrapper<String>> deleteSong(
            @PathVariable Long songId,
            @RequestBody @Valid DeleteSongRequest request
    ) {
        songService.deleteSong(songId, request.getReason());
        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Song deleted successfully and artist notified.")
                        .build()
        );
    }


}
