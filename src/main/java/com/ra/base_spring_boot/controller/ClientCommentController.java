package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.CommentRequest;
import com.ra.base_spring_boot.dto.req.UpdateCommentRequest;
import com.ra.base_spring_boot.dto.resp.CommentResponseDTO;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.services.IClientCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/client/comments")
@RequiredArgsConstructor
public class ClientCommentController {

    private final IClientCommentService commentService;

    @GetMapping("/song/{songId}")
    public ResponseEntity<ResponseWrapper<PageResponse<CommentResponseDTO>>> getCommentsBySong(
            @PathVariable Long songId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return ResponseEntity.ok(
                ResponseWrapper.<PageResponse<CommentResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(commentService.getCommentsBySong(songId, page, size, sortBy, sortDir))
                        .build()
        );
    }
    @PostMapping
    public ResponseEntity<ResponseWrapper<CommentResponseDTO>> addComment(@Valid @RequestBody CommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.<CommentResponseDTO>builder()
                        .status(HttpStatus.CREATED)
                        .code(HttpStatus.CREATED.value())
                        .data(commentService.addComment(request))
                        .build());
    }
    @PutMapping("/{commentId}")
    public ResponseEntity<ResponseWrapper<CommentResponseDTO>> updateComment(
            @Valid
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest request
    ) {
        return ResponseEntity.ok(
                ResponseWrapper.<CommentResponseDTO>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(commentService.updateComment(commentId, request.getContent()))
                        .build()
        );
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResponseWrapper<String>> deleteComment(
            @PathVariable Long commentId
    ) {

        commentService.deleteComment(commentId);
        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.NO_CONTENT)
                        .code(HttpStatus.NO_CONTENT.value())
                        .data("Comment deleted successfully")
                        .build()
        );
    }

}
