package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.CommentRequest;
import com.ra.base_spring_boot.dto.resp.CommentResponseDTO;
import com.ra.base_spring_boot.dto.resp.PageResponse;

public interface IClientCommentService {


    PageResponse<CommentResponseDTO> getCommentsBySong(Long songId, int page, int size, String sortBy, String sortDir);

    CommentResponseDTO addComment(CommentRequest request);

    CommentResponseDTO updateComment(Long commentId, String newContent);

    void deleteComment(Long commentId);


}
