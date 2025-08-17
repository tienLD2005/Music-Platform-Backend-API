package com.ra.base_spring_boot.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequest {


    @NotNull(message = "Song ID is required")
    private Long songId;

    @NotBlank(message = "Content must not be blank")
    private String content;

    private Long parentId;
}
