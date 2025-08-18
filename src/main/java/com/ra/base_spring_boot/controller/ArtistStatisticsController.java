package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.services.impl.ArtistStatisticsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/artists-statistics")
@RequiredArgsConstructor
public class ArtistStatisticsController {
    private final ArtistStatisticsServiceImpl artistStatisticsService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<?>> getArtistStatistics() {
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(artistStatisticsService.getArtistStatistics())
                        .build()
        );
    }
}
