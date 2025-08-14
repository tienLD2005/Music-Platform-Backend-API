package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.AlbumRequest;
import com.ra.base_spring_boot.dto.resp.AlbumResponseDTO;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.services.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping()
    @PreAuthorize("hasAuthority('ROLE_ARTIST')")
    public ResponseEntity<ResponseWrapper<PageResponse<AlbumResponseDTO>>> getMyAlbums(
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
                        .data(albumService.getMyAlbums(title, page, size, sortBy, sortDir))
                        .build()
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ARTIST')")
    public ResponseEntity<ResponseWrapper<AlbumResponseDTO>> createAlbum(@Valid @ModelAttribute AlbumRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.createAlbum(request));
    }

    @PutMapping("/{albumId}")
    @PreAuthorize("hasAuthority('ROLE_ARTIST')")
    public ResponseEntity<ResponseWrapper<AlbumResponseDTO>> updateAlbum(
            @Valid
            @PathVariable Long albumId,
            @ModelAttribute AlbumRequest request) {
        return ResponseEntity.ok(albumService.updateAlbum(albumId, request));
    }

    @DeleteMapping("/{albumId}")
    @PreAuthorize("hasAuthority('ROLE_ARTIST')")
    public ResponseEntity<ResponseWrapper<String>> deleteAlbum(@PathVariable Long albumId) {
        return ResponseEntity.ok(albumService.deleteAlbum(albumId));
    }
}
