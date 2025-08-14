package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.req.AddSongToPlaylistReq;
import com.ra.base_spring_boot.dto.req.PlaylistReq;
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

    // 1. search
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> listPlaylistsOfUser(
            @PathVariable Long userId,
            @RequestParam(name = "q", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Page<PlaylistResp> data = playlistService.searchOfUser(userId, keyword, page, size, sortBy, direction);

        Map<String, Object> body = new HashMap<>();
        body.put("items", data.getContent());

        Map<String, Object> meta = new HashMap<>();
        meta.put("page", data.getNumber());
        meta.put("size", data.getSize());
        meta.put("totalElements", data.getTotalElements());
        meta.put("totalPages", data.getTotalPages());
        meta.put("sortBy", sortBy);
        meta.put("direction", direction);

        body.put("meta", meta);

        return ResponseEntity.ok(body);
    }

    // 2. new playlist
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<PlaylistResp> createPlaylist(
            @PathVariable Long userId,
            @RequestBody @Valid PlaylistReq request) {
        PlaylistResp resp = playlistService.createPlaylist(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    // 3. add
    @PostMapping("/{playlistId}/songs")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> addSongToPlaylist(
            @PathVariable Long playlistId,
            @RequestBody @Valid AddSongToPlaylistReq request) {
        playlistService.addSongToPlaylist(playlistId, request.getSongId());

        Map<String, String> resp = new HashMap<>();
        resp.put("message", "Song added successfully");
        return ResponseEntity.ok(resp);
    }

    // 4. dele
    @DeleteMapping("/{playlistId}/songs/{songId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> removeSongFromPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long songId) {
        playlistService.removeSongFromPlaylist(playlistId, songId);

        Map<String, String> resp = new HashMap<>();
        resp.put("message", "Song removed successfully");
        return ResponseEntity.ok(resp);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
