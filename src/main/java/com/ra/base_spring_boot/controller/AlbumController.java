package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.AlbumResponseDTO;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.services.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/albums")

@PreAuthorize("hasRole('ROLE_ARTIST')")
@RequiredArgsConstructor
public class AlbumController {
    @Autowired
    private final AlbumService albumService;

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<ResponseWrapper<PageResponse<AlbumResponseDTO>>> getAlbumsByArtist(
            @PathVariable Long artistId,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "releaseDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return ResponseEntity.ok(
                ResponseWrapper.<PageResponse<AlbumResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(albumService.getAlbumsByArtist(artistId, title, page, size, sortBy, sortDir))
                        .build()
        );
    }
}
