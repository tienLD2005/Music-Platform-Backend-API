package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.SongStatisticsFilterRequestDTO;
import com.ra.base_spring_boot.dto.resp.SongStatisticsResponseDTO;
import com.ra.base_spring_boot.services.ISongStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/admin/song-statistics")
@RequiredArgsConstructor
public class SongStatisticsController {

    private final ISongStatisticsService songStatisticsService;

    @GetMapping("/statistics")
    public ResponseWrapper<SongStatisticsResponseDTO> getSongStatistics(
            @RequestParam(required = false) Long artistId,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Long albumId,
            @RequestParam(defaultValue = "plays") String sortBy
    ) {
        SongStatisticsFilterRequestDTO filter = new SongStatisticsFilterRequestDTO(
                artistId, genreId, albumId, sortBy
        );

        SongStatisticsResponseDTO data = songStatisticsService.getSongStatistics(filter);

        return ResponseWrapper.<SongStatisticsResponseDTO>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data(data)
                .build();
    }
}
