package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.AlbumRequest;
import com.ra.base_spring_boot.dto.req.FormSongRequest;
import com.ra.base_spring_boot.dto.resp.*;
import com.ra.base_spring_boot.model.base.Pagination;
import com.ra.base_spring_boot.services.IAlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final IAlbumService albumService;

    @GetMapping("/my-albums")
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
    public ResponseEntity<ResponseWrapper<String>> deleteAlbum(@PathVariable Long albumId){
        return ResponseEntity.ok(albumService.deleteAlbum(albumId));
    }

        @GetMapping("/{albumId}/songs")
    public ResponseEntity<?> getSongsByAlbum(@PathVariable Long albumId,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(defaultValue = "createdAt") String sortBy,
                                             @RequestParam(defaultValue = "DESC") String direction) {
        Page<ResponseSong> songsPage = albumService.getSongsByAlbum(albumId, page - 1, size, sortBy, direction);

        PaginatedResponse<ResponseSong> paginated = new PaginatedResponse<>();
        paginated.setItems(songsPage.getContent());
        paginated.setPagination(new Pagination(
                songsPage.getNumber() + 1,
                songsPage.getSize(),
                songsPage.getTotalPages(),
                songsPage.getTotalElements()
        ));
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(paginated)
                        .build()
        );
    }

    @PostMapping("/{albumId}/songs")
    public ResponseEntity<?> addSongToAlbum(@PathVariable Long albumId,
                                            @ModelAttribute @Valid FormSongRequest request,
                                            Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.CREATED)
                        .code(HttpStatus.CREATED.value())
                        .data(albumService.addSongToAlbum(albumId, request, user.getUsername()))
                        .build()
        );
    }

    @DeleteMapping("/{albumId}/songs/{songId}")
    public ResponseEntity<?> deleteSongFromAlbum(@PathVariable Long albumId,
                                                 @PathVariable Long songId,
                                                 Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        albumService.deleteSongFromAlbum(albumId, songId, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(null)
                        .build()
        );
    }

    @GetMapping("/top-trending")
    public ResponseEntity<?> getTopTrendingAlbums(@RequestParam(defaultValue = "5") int limit) {
        List<AlbumResponse> albums = albumService.getTopTrendingAlbums(limit);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(albums)
                        .build()
        );
    }
}
