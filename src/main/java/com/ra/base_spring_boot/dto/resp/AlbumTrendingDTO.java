package com.ra.base_spring_boot.dto.resp;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumTrendingDTO {
    private Long id;
    private String title;
    private String coverImage;
    private String artistFirstName;
    private String artistLastName;
    private Long songCount;
    private Long totalViews;
    private Long songHistoryCount;

    public Long getTotalPlays() {
        return (totalViews != null ? totalViews : 0) +
               (songHistoryCount != null ? songHistoryCount : 0);
    }
}

