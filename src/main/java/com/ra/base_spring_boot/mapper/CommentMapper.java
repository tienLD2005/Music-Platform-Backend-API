package com.ra.base_spring_boot.mapper;

import com.ra.base_spring_boot.dto.resp.CommentResponseDTO;
import com.ra.base_spring_boot.model.Comment;

import java.util.stream.Collectors;

public class CommentMapper {

    public static CommentResponseDTO toDto(Comment comment) {
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .userId(comment.getUser().getId())
                .userName(comment.getUser().getFirstName() + " " + comment.getUser().getLastName())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .replies(comment.getReplies() != null
                        ? comment.getReplies().stream()
                        .map(CommentMapper::toDto)
                        .collect(Collectors.toList())
                        : null)
                .build();
    }
}
