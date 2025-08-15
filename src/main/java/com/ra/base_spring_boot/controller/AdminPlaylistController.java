package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.AddSongToPlaylistReq;
import com.ra.base_spring_boot.dto.req.PlaylistReq;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.PlaylistResp;
import com.ra.base_spring_boot.services.IPlaylistService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users/{userId}/playlists")
public class AdminPlaylistController {

    private final IPlaylistService playlistService;

    public AdminPlaylistController(IPlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    // 1. Search playlists of user
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<PageResponse<PlaylistResp>>> listPlaylistsOfUser(
            @PathVariable Long userId,
            @RequestParam(name = "q", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        PageResponse<PlaylistResp> pageResponse = playlistService.searchOfUser(userId, keyword, page, size, sortBy, direction);

        ResponseWrapper<PageResponse<PlaylistResp>> resp = ResponseWrapper.<PageResponse<PlaylistResp>>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data(pageResponse)
                .build();

        return ResponseEntity.ok(resp);
    }

    // 2. Create playlist
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<PlaylistResp>> createPlaylist(
            @PathVariable Long userId,
            @RequestBody @Valid PlaylistReq request) {
        PlaylistResp playlist = playlistService.createPlaylist(userId, request);

        ResponseWrapper<PlaylistResp> resp = ResponseWrapper.<PlaylistResp>builder()
                .status(HttpStatus.CREATED)
                .code(HttpStatus.CREATED.value())
                .data(playlist)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    // 3. Add song
    @PostMapping("/{playlistId}/songs")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<String>> addSongToPlaylist(
            @PathVariable Long playlistId,
            @RequestBody @Valid AddSongToPlaylistReq request) {
        playlistService.addSongToPlaylist(playlistId, request.getSongId());

        ResponseWrapper<String> resp = ResponseWrapper.<String>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data("Song added successfully")
                .build();

        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{playlistId}/songs/{songId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<String>> removeSongFromPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long songId) {
        playlistService.removeSongFromPlaylist(playlistId, songId);

        ResponseWrapper<String> resp = ResponseWrapper.<String>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data("Song removed successfully")
                .build();

        return ResponseEntity.ok(resp);
    }

}
