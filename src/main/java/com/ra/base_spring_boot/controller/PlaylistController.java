package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.AddSongToPlaylistReq;
import com.ra.base_spring_boot.dto.req.PlaylistReq;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.PlaylistResp;
import com.ra.base_spring_boot.dto.resp.SongResponse;
import com.ra.base_spring_boot.services.IPlaylistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/playlists")
public class PlaylistController {

    private final IPlaylistService playlistService;

    public PlaylistController(IPlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<PageResponse<PlaylistResp>>> listPlaylistsOfCurrentUser(
            @RequestParam(name = "q", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        PageResponse<PlaylistResp> pageResponse = playlistService.searchOfCurrentUser(keyword, page, size, sortBy, direction);

        return ResponseEntity.ok(
                ResponseWrapper.<PageResponse<PlaylistResp>>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(pageResponse)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<PlaylistResp>> createPlaylist(
            @RequestBody @Valid PlaylistReq request) {
        PlaylistResp playlist = playlistService.createPlaylist(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseWrapper.<PlaylistResp>builder()
                        .status(HttpStatus.CREATED)
                        .code(HttpStatus.CREATED.value())
                        .data(playlist)
                        .build()
        );
    }

    @PostMapping("/{playlistId}/songs")
    public ResponseEntity<ResponseWrapper<String>> addSongToPlaylist(
            @PathVariable Long playlistId,
            @RequestBody @Valid AddSongToPlaylistReq request) {
        playlistService.addSongToPlaylist(playlistId, request.getSongId());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.CREATED)
                        .code(HttpStatus.CREATED.value())
                        .data("Song added successfully")
                        .build()
        );
    }

    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<ResponseWrapper<String>> removeSongFromPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long songId) {
        playlistService.removeSongFromPlaylist(playlistId, songId);

        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data("Song removed successfully")
                        .build()
        );
    }

    @GetMapping("/{playlistId}/songs")
    public ResponseEntity<ResponseWrapper<List<SongResponse>>> getSongsInPlaylist(
            @PathVariable Long playlistId
    ) {
        List<SongResponse> songs = playlistService.getSongsInPlaylist(playlistId);

        return ResponseEntity.ok(
                ResponseWrapper.<List<SongResponse>>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(songs)
                        .build()
        );
    }
    @PutMapping("/{playlistId}")
    public ResponseEntity<ResponseWrapper<PlaylistResp>> updatePlaylist(
            @PathVariable Long playlistId,
            @RequestBody @Valid PlaylistReq request) {
        PlaylistResp updated = playlistService.updatePlaylist(playlistId, request);

        return ResponseEntity.ok(
                ResponseWrapper.<PlaylistResp>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(updated)
                        .build()
        );
    }

    @DeleteMapping("/{playlistId}")
    public ResponseEntity<ResponseWrapper<String>> deletePlaylist(@PathVariable Long playlistId) {
        playlistService.deletePlaylist(playlistId);

        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data("Playlist deleted successfully")
                        .build()
        );
    }
}

