package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.SongResponse;
import com.ra.base_spring_boot.dto.resp.TopSongDTO;
import com.ra.base_spring_boot.services.ISongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/songs")
@RequiredArgsConstructor
public class SongController {
    private final ISongService songService;

    @GetMapping("/top-week")
    public ResponseEntity<?> getTop15SongsOfWeek() {
        List<TopSongDTO> topSongs = songService.getTop15SongsOfWeek();
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(topSongs)
                        .build()
        );
    }

}
