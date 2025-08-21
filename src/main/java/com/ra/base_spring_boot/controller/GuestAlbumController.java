package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.AlbumResponse;
import com.ra.base_spring_boot.services.IAlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/guest/albums")
@RequiredArgsConstructor
public class GuestAlbumController {

    private final IAlbumService albumService;

    @GetMapping
    public ResponseEntity<?> getAlbums(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(defaultValue = "title") String sortBy,
                                       @RequestParam(defaultValue = "asc") String sortDir,
                                       @RequestParam(required = false) String keyword) {

        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(albumService.getAllAlbums(page, size, sortBy, sortDir, keyword))
                        .build()
        );
    }

    @GetMapping("/top")
    public ResponseEntity<?> getTopAlbums(@RequestParam(defaultValue = "week") String period) {

        return  ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(albumService.getTopAlbums(period))
                        .build()
        );
    }

    @GetMapping("/featured")
    public ResponseEntity<?> getFeaturedAlbums() {

        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(albumService.findFeaturedAlbums())
                        .build()
        );
    }

    @GetMapping("/{artistId}")
    public ResponseEntity<?> getAlbumsByArtist(@PathVariable Long artistId,
                                               @RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(required = false, defaultValue = "") String keyword,
                                               @RequestParam(defaultValue = "desc") String sortDir,
                                               @RequestParam(defaultValue = "false") boolean isPremium) {

        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(albumService.getAlbumsByArtist(artistId, page, size, keyword, sortDir, isPremium))
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
