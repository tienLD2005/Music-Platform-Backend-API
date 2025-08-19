package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.ReactionRequestDTO;
import com.ra.base_spring_boot.dto.resp.ReactionResponseDTO;
import com.ra.base_spring_boot.model.CommentReaction;
import com.ra.base_spring_boot.model.constants.ReactionEnum;
import com.ra.base_spring_boot.services.ICommentReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentReactionController {

    private final ICommentReactionService commentReactionService;

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/{commentId}/reactions")
    public ResponseEntity<ResponseWrapper<ReactionResponseDTO>> reactToComment(
            @PathVariable Long commentId,
            @RequestBody ReactionRequestDTO request
    ) {
        CommentReaction reaction = commentReactionService.reactToComment(commentId, ReactionEnum.valueOf(request.getReactionEnum()));

        ReactionResponseDTO responseDTO = ReactionResponseDTO.builder()
                .id(reaction.getId())
                .userId(reaction.getUser().getId())
                .commentId(commentId)
                .reactionEnum(reaction.getReactionEnum())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseWrapper.<ReactionResponseDTO>builder()
                        .status(HttpStatus.CREATED)
                        .code(HttpStatus.CREATED.value())
                        .data(responseDTO)
                        .build()
        );
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @DeleteMapping("/{commentId}/reactions")
    public ResponseEntity<ResponseWrapper<String>> removeReaction(@PathVariable Long commentId) {
        commentReactionService.removeReaction(commentId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.NO_CONTENT)
                        .code(HttpStatus.NO_CONTENT.value())
                        .data("Reaction removed successfully")
                        .build()
        );
    }

    @GetMapping("/{commentId}/reactions")
    public ResponseEntity<ResponseWrapper<List<ReactionResponseDTO>>> getReactions(@PathVariable Long commentId) {
        List<ReactionResponseDTO> reactions = commentReactionService.getReactionsByComment(commentId)
                .stream()
                .map(r -> ReactionResponseDTO.builder()
                        .id(r.getId())
                        .userId(r.getUser().getId())
                        .commentId(r.getComment().getId())
                        .reactionEnum(r.getReactionEnum())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ResponseWrapper.<List<ReactionResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(reactions)
                        .build()
        );
    }

    @GetMapping("/{commentId}/reactions/count")
    public ResponseEntity<ResponseWrapper<Long>> countReactions(@PathVariable Long commentId) {
        Long count = commentReactionService.countReactions(commentId);

        return ResponseEntity.ok(
                ResponseWrapper.<Long>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(count)
                        .build()
        );
    }
}