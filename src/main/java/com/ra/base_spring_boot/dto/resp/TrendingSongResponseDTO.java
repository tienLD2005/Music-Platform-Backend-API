package com.ra.base_spring_boot.dto.resp;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrendingSongResponseDTO {
    private Long songId;
    private String title;
    private String artistName;
    private Long playCount;
    private Long uniqueListeners;
    private Long positiveReactions;
    private Long negativeReactions;
    private Long downloadCount;
    private Long playlistAddCount;
    private Long commentCount;
    private Double trendingScore;
}
