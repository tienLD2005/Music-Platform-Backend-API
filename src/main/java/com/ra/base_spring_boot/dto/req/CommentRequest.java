package com.ra.base_spring_boot.dto.req;

import lombok.Data;

@Data
public class CommentRequest {
    private Long songId;
    private String content;
    private Long parentId;
}
