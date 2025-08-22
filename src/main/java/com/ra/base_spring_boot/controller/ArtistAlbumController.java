package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.AlbumRequest;
import com.ra.base_spring_boot.dto.resp.*;
import com.ra.base_spring_boot.dto.req.FormSongRequest;
import com.ra.base_spring_boot.security.principle.MyUserDetails;
import com.ra.base_spring_boot.services.IAlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/artist/albums")
@RequiredArgsConstructor
public class ArtistAlbumController {

    private final IAlbumService albumService;

    @GetMapping("/my-album")
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseWrapper<AlbumResponseDTO>> createAlbum(@Valid @ModelAttribute AlbumRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.createAlbum(request));
    }

    @PutMapping(value = "/{albumId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseWrapper<AlbumResponseDTO>> updateAlbum(
            @Valid
            @PathVariable Long albumId,
            @ModelAttribute AlbumRequest request) {
        return ResponseEntity.ok(albumService.updateAlbum(albumId, request));
    }

    @DeleteMapping("/{albumId}")
    public ResponseEntity<ResponseWrapper<String>> deleteAlbum(@PathVariable Long albumId){
        return ResponseEntity.ok(albumService.deleteAlbum(albumId));
    }

    @GetMapping("/{albumId}/songs")
    public ResponseEntity<?> getSongsByAlbum(@PathVariable Long albumId,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(defaultValue = "createdAt") String sortBy,
                                             @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(albumService.getSongsByAlbum(albumId, page, size, sortBy, direction))
                        .build()
        );
    }

    @PostMapping( value = "/{albumId}/songs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addSongToAlbum(@PathVariable Long albumId,
                                            @ModelAttribute @Valid FormSongRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.CREATED)
                        .code(HttpStatus.CREATED.value())
                        .data(albumService.addSongToAlbum(albumId, request))
                        .build()
        );
    }

    @DeleteMapping("/{albumId}/songs/{songId}")
    public ResponseEntity<?> deleteSongFromAlbum(@PathVariable Long albumId,
                                                 @PathVariable Long songId,
                                                 @AuthenticationPrincipal MyUserDetails principal) {
        String message = albumService.deleteSongFromAlbum(albumId, songId, principal);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(message)
                        .build()
        );
    }
}
