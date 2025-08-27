package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.SongStatisticsFilterRequestDTO;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.CommentStatisticsResponseDTO;
import com.ra.base_spring_boot.dto.resp.SongStatisticsResponseDTO;
import com.ra.base_spring_boot.dto.resp.SubscriptionPlanStatisticDTO;
import com.ra.base_spring_boot.services.*;
import com.ra.base_spring_boot.services.impl.UserStatisticsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics Management")
public class StatisticManagementController {

    private final UserStatisticsService userStatisticsService;
    private final ISongStatisticsService songStatisticsService;
    private final IArtistStatisticsService artistStatisticsService;
    private final IAlbumStatisticsService albumStatisticsService;
    private final ISubscriptionPlanStatisticService statisticService;
    private final IGenreStatisticService genreStatisticService;
    private final ICommentStatisticsService commentStatisticsService;

    @GetMapping("/users/status")
    public ResponseEntity<ResponseWrapper<Map<String, Long>>> getUserCountByStatus() {
        Map<String, Long> data = userStatisticsService.getUserCountByStatus();
        return ResponseEntity.ok(
                ResponseWrapper.<Map<String, Long>>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/users/account-types")
    public ResponseEntity<ResponseWrapper<Map<String, Long>>> getUserCountByAccountType() {
        Map<String, Long> data = userStatisticsService.getUserCountByAccountType();
        return ResponseEntity.ok(
                ResponseWrapper.<Map<String, Long>>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/albums")
    public ResponseEntity<ResponseWrapper<Map<String, Object>>> getAlbumStatistics() {
        Map<String, Object> data = albumStatisticsService.getAlbumStatistics();
        return ResponseEntity.ok(
                ResponseWrapper.<Map<String, Object>>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/artists")
    public ResponseEntity<ResponseWrapper<Map<String, Object>>> getArtistStatistics() {
        return ResponseEntity.ok(
                ResponseWrapper.<Map<String, Object>>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(artistStatisticsService.getArtistStatistics())
                        .build()
        );
    }

    @GetMapping("/songs")
    public ResponseEntity<ResponseWrapper<SongStatisticsResponseDTO>> getSongStatistics(
            @RequestParam(required = false) Long artistId,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Long albumId,
            @RequestParam(defaultValue = "plays") String sortBy
    ) {
        SongStatisticsFilterRequestDTO filter = new SongStatisticsFilterRequestDTO(
                artistId, genreId, albumId, sortBy
        );
        SongStatisticsResponseDTO data = songStatisticsService.getSongStatistics(filter);

        return ResponseEntity.ok(
                ResponseWrapper.<SongStatisticsResponseDTO>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(data)
                        .build()
        );
    }

    @GetMapping("subscriptions")
    public ResponseEntity<ResponseWrapper<PageResponse<SubscriptionPlanStatisticDTO>>> getStatistics(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<SubscriptionPlanStatisticDTO> stats = statisticService.getStatistics(page, size);

        ResponseWrapper<PageResponse<SubscriptionPlanStatisticDTO>> response =
                ResponseWrapper.<PageResponse<SubscriptionPlanStatisticDTO>>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(stats)
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/genre")
    public  ResponseEntity<ResponseWrapper<?>> getGenreStatistics() {
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(genreStatisticService.getGenreStatistics())
                        .build()
        );
    }

    @GetMapping("/comment")
    public ResponseEntity<ResponseWrapper<CommentStatisticsResponseDTO>> getCommentStatistics() {
        return ResponseEntity.ok(
                ResponseWrapper.<CommentStatisticsResponseDTO>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(commentStatisticsService.getCommentsStatistics())
                        .build()
        );
    }

}
