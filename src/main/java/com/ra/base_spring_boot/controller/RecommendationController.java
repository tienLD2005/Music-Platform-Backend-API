package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.SongResponse;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.services.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> recommendSongs(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<SongResponse> recommendations = recommendationService.recommendSongs(userId, limit);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                                .status(HttpStatus.OK)
                                .code(200)
                                .data(recommendations)
                                .build()
        );
    }
}
