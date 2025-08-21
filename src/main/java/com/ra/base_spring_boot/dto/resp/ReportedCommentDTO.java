package com.ra.base_spring_boot.dto.resp;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportedCommentDTO {
    private Long id;
    private String content;
}