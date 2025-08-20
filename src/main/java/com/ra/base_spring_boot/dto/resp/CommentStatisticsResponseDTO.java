package com.ra.base_spring_boot.dto.resp;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentStatisticsResponseDTO {
    private Long totalComments;
    private Map<String, Long> commentsBySong;
    private List<ReportedCommentDTO> reportedComments;
    private Map<String, Long> commentsOverTime;
    private List<PopularCommentDTO> popularComments;
}
