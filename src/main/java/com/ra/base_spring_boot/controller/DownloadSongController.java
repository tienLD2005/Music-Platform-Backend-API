package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.req.DownloadSongRequest;
import com.ra.base_spring_boot.dto.resp.DownloadResponse;
import com.ra.base_spring_boot.dto.resp.DownloadedSongResponse;
import com.ra.base_spring_boot.security.principle.MyUserDetails;
import com.ra.base_spring_boot.services.IDownloadSongService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/download-song")
@RequiredArgsConstructor
@Tag(name = "Download Management")
public class DownloadSongController {

    private final IDownloadSongService downloadService;

    @PostMapping("/song")
    public ResponseEntity<DownloadResponse> downloadSong(
            @AuthenticationPrincipal MyUserDetails userDetails,
            @Valid @RequestBody DownloadSongRequest request) {

        DownloadResponse response = downloadService.downloadSong(userDetails.getId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/songs")
    public ResponseEntity<Page<DownloadedSongResponse>> getDownloadedSongs(
            @AuthenticationPrincipal MyUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<DownloadedSongResponse> downloads = downloadService.getDownloadedSongs(userDetails.getId(), pageable);
        return ResponseEntity.ok(downloads);
    }

    @GetMapping("/songs/sorted")
    public ResponseEntity<List<DownloadedSongResponse>> getDownloadedSongsSorted(
            @AuthenticationPrincipal MyUserDetails userDetails,
            @RequestParam(defaultValue = "date") String sortBy) {

        List<DownloadedSongResponse> downloads = downloadService.getDownloadedSongsSorted(userDetails.getId(), sortBy);
        return ResponseEntity.ok(downloads);
    }

    @DeleteMapping("/songs/{songId}")
    public ResponseEntity<DownloadResponse> removeDownloadedSong(
            @AuthenticationPrincipal MyUserDetails userDetails,
            @PathVariable Long songId) {

        DownloadResponse response = downloadService.removeDownloadedSong(userDetails.getId(), songId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/songs/{songId}/check")
    public ResponseEntity<Boolean> checkDownloadStatus(
            @AuthenticationPrincipal MyUserDetails userDetails,
            @PathVariable Long songId) {

        boolean isDownloaded = downloadService.isAlreadyDownloaded(userDetails.getId(), songId);
        return ResponseEntity.ok(isDownloaded);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getDownloadCount(
            @AuthenticationPrincipal MyUserDetails userDetails) {

        long count = downloadService.getDownloadCount(userDetails.getId());
        return ResponseEntity.ok(count);
    }
}

