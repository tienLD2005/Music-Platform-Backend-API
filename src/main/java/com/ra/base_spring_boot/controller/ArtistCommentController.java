package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.CommentRequest;
import com.ra.base_spring_boot.dto.resp.CommentResponseDTO;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.services.IArtistCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/artist/comments")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ARTIST')")
public class ArtistCommentController {

    private final IArtistCommentService artistCommentService;

    @GetMapping("/song/{songId}")
    public ResponseEntity<ResponseWrapper<PageResponse<CommentResponseDTO>>> getCommentsForOwnedSong(
            @PathVariable Long songId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return ResponseEntity.ok(
                ResponseWrapper.<PageResponse<CommentResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(artistCommentService.getCommentsForOwnedSong(songId, page, size, sortBy, sortDir))
                        .build()
        );
    }

    @PostMapping("/reply")
    public ResponseEntity<ResponseWrapper<CommentResponseDTO>> replyToComment(
            @Valid @RequestBody CommentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.<CommentResponseDTO>builder()
                        .status(HttpStatus.CREATED)
                        .code(HttpStatus.CREATED.value())
                        .data(artistCommentService.replyToComment(request))
                        .build());
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResponseWrapper<String>> deleteCommentAsArtist(
            @PathVariable Long commentId,
            @RequestParam(defaultValue = "false") boolean confirm
    ) {
        artistCommentService.deleteCommentAsArtist(commentId, confirm);
        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data("Comment deleted successfully")
                        .build()
        );
    }
}
