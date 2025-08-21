package com.ra.base_spring_boot.dto.resp;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PopularCommentDTO {
    private Long id;
    private String content;
    private Long likes;
}
