package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.services.impl.AlbumStatisticsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/album-statistics")
@RequiredArgsConstructor
public class AlbumStatisticsController {
    private final AlbumStatisticsServiceImpl albumStatisticsService;

    @GetMapping
    public ResponseWrapper<Map<String, Object>> getAlbumStatistics() {
        Map<String, Object> data = albumStatisticsService.getAlbumStatistics();
        return ResponseWrapper.<Map<String, Object>>builder()
                .status(HttpStatus.OK)
                .code(200)
                .data(data)
                .build();
    }
}

