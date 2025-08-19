package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.SongReactionResponseDto;
import com.ra.base_spring_boot.mapper.SongReactionMapper;
import com.ra.base_spring_boot.model.SongReaction;
import com.ra.base_spring_boot.model.constants.ReactionEnum;
import com.ra.base_spring_boot.security.principle.MyUserDetails;
import com.ra.base_spring_boot.services.ISongReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/songs")
@RequiredArgsConstructor
public class SongReactionController {

    private final ISongReactionService songReactionService;
    private final SongReactionMapper songReactionMapper;

    @PostMapping("/{songId}/reactions")
    public ResponseEntity<ResponseWrapper<SongReactionResponseDto>> reactToSong(
            @PathVariable Long songId,
            @RequestParam ReactionEnum reaction,
            @AuthenticationPrincipal MyUserDetails userDetails
    ) {

        SongReaction songReaction = songReactionService.reactToSong(songId, userDetails.getId(), reaction);
        SongReactionResponseDto responseDto = songReactionMapper.toResponseDto(songReaction);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseWrapper.<SongReactionResponseDto>builder()
                        .status(HttpStatus.CREATED)
                        .code(HttpStatus.CREATED.value())
                        .data(responseDto)
                        .build()
        );
    }

    @DeleteMapping("/{songId}/reactions")
    public ResponseEntity<ResponseWrapper<Void>> removeReaction(
            @PathVariable Long songId,
            @AuthenticationPrincipal MyUserDetails userDetails
    ) {
        songReactionService.removeReaction(songId, userDetails.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                ResponseWrapper.<Void>builder()
                        .status(HttpStatus.NO_CONTENT)
                        .code(HttpStatus.NO_CONTENT.value())
                        .data(null)
                        .build()
        );
    }

    @GetMapping("/{songId}/reactions/me")
    public ResponseEntity<ResponseWrapper<ReactionEnum>> getUserReaction(
            @PathVariable Long songId,
            @AuthenticationPrincipal MyUserDetails userDetails
    ) {
        ReactionEnum reaction = songReactionService.getUserReaction(songId, userDetails.getId());
        return ResponseEntity.ok(
                ResponseWrapper.<ReactionEnum>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(reaction)
                        .build()
        );
    }

    @GetMapping("/{songId}/reactions")
    public ResponseEntity<ResponseWrapper<Map<String, Long>>> getReactionsCount(
            @PathVariable Long songId
    ) {
        Map<ReactionEnum, Long> counts = songReactionService.getReactionsCount(songId);
        Map<String, Long> stringKeyMap = songReactionMapper.toStringKeyMap(counts);

        return ResponseEntity.ok(
                ResponseWrapper.<Map<String, Long>>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(stringKeyMap)
                        .build()
        );
    }
}
