package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.services.IDownloadSongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/download-songs")
@RequiredArgsConstructor
public class DownloadSongController {
    private final IDownloadSongService downloadSongService;
    @PostMapping("/download")
    public ResponseEntity<String> downloadSong(
            @RequestParam String url,
            @RequestParam String fileName) {

        String filePath = "C:\\music\\" + fileName;
        boolean success = downloadSongService.downloadSong(url, filePath);

        if (success) {
            return ResponseEntity.ok("Downloaded successfully to: " + filePath);
        } else {
            return ResponseEntity.badRequest().body("Download failed");
        }
    }
}
