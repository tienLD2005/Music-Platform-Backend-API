package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.GenreTrendingDTO;
import com.ra.base_spring_boot.services.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/g-genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/trending")
    public ResponseEntity<?> getTrendingGenres(
            @RequestParam(defaultValue = "week") String period,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(
                ResponseWrapper.<List<GenreTrendingDTO>>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(genreService.getTrendingGenres(period, limit))
                        .build()
        );
    }

}