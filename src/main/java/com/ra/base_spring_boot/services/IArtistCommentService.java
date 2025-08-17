package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.CommentRequest;
import com.ra.base_spring_boot.dto.resp.CommentResponseDTO;
import com.ra.base_spring_boot.dto.resp.PageResponse;

public interface IArtistCommentService {
    PageResponse<CommentResponseDTO> getCommentsForOwnedSong(Long songId, int page, int size, String sortBy, String sortDir);
    CommentResponseDTO replyToComment(CommentRequest request);
    void deleteCommentAsArtist(Long commentId, boolean confirm);
}
