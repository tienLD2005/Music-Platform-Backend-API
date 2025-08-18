package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
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

    @PostMapping("/{songId}/reactions")
    public ResponseEntity<ResponseWrapper<SongReaction>> reactToSong(
            @PathVariable Long songId,
            @RequestParam ReactionEnum reaction,
            @AuthenticationPrincipal MyUserDetails userDetails
    ) {
        SongReaction songReaction = songReactionService.reactToSong(songId, userDetails.getId(), reaction);
        return ResponseEntity.ok(
                ResponseWrapper.<SongReaction>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(songReaction)
                        .build()
        );
    }

    @DeleteMapping("/{songId}/reactions")
    public ResponseEntity<ResponseWrapper<Void>> removeReaction(
            @PathVariable Long songId,
            @AuthenticationPrincipal MyUserDetails userDetails
    ) {
        songReactionService.removeReaction(songId, userDetails.getId());
        return ResponseEntity.ok(
                ResponseWrapper.<Void>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
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
    public ResponseEntity<ResponseWrapper<Map<ReactionEnum, Long>>> getReactionsCount(
            @PathVariable Long songId
    ) {
        Map<ReactionEnum, Long> counts = songReactionService.getReactionsCount(songId);
        return ResponseEntity.ok(
                ResponseWrapper.<Map<ReactionEnum, Long>>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(counts)
                        .build()
        );
    }
}
