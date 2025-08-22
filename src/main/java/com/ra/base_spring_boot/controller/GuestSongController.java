package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.TopSongDTO;
import com.ra.base_spring_boot.dto.resp.TopSongOfWeek;
import com.ra.base_spring_boot.dto.resp.TrendingSongResponseDTO;
import com.ra.base_spring_boot.services.ISongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/guest/songs")
@RequiredArgsConstructor
public class GuestSongController {
    private final ISongService songService;

    @GetMapping("/top-week")
    public ResponseEntity<?> getTop15SongsOfWeek() {
        List<TopSongOfWeek> topSongs = songService.getTop15SongsOfWeek();
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(topSongs)
                        .build()
        );
    }

    @GetMapping("/top-all-time")
    public ResponseEntity<?> getTopSongsAllTime(@RequestParam(defaultValue = "15") int limit) {
        List<TopSongDTO> topSongs = songService.getTopSongsAllTime(limit);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(topSongs)
                        .build()
        );
    }

    @GetMapping("/trending-this-month")
    public ResponseEntity<?> getTrendingSongs(@RequestParam(defaultValue = "15") int limit) {
        List<TopSongDTO> trendingSongs = songService.getTrendingSongsThisMonth(limit);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(trendingSongs)
                        .build()
        );
    }

}
