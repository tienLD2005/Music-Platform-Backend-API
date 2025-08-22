package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.SongHistoryResponse;
import com.ra.base_spring_boot.model.SongHistory;
import com.ra.base_spring_boot.security.principle.MyUserDetails;
import com.ra.base_spring_boot.services.ISongHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/song-history")
@RequiredArgsConstructor
public class SongHistoryController {

    private final ISongHistoryService songHistoryService;

    @GetMapping("/recent")
    public ResponseEntity<ResponseWrapper<PageResponse<SongHistoryResponse>>> getRecent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal MyUserDetails principal
    ) {
        PageResponse<SongHistoryResponse> data =
                songHistoryService.getRecentHistory(principal.getId(), page, size);

        ResponseWrapper<PageResponse<SongHistoryResponse>> body = ResponseWrapper.<PageResponse<SongHistoryResponse>>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data(data)
                .build();

        return ResponseEntity.ok(body);
    }

    @PostMapping("/play/{songId}")
    public ResponseEntity<ResponseWrapper<?>> addPlay(
            @PathVariable Long songId,
            @AuthenticationPrincipal MyUserDetails principal
    ) {
        SongHistoryResponse songHistory = songHistoryService.addPlay(principal.getId(), songId);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.CREATED)
                        .code(HttpStatus.CREATED.value())
                        .data(songHistory)
                        .build()
        );
    }
}
