package com.ra.base_spring_boot.dto.resp;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopSongOfWeek {
    private Long id;
    private String title;
    private LocalTime duration;
    private String fileUrl;
    private Long views;
    private Long downloadCount;
    private Long listenCount;

    public Long getTotalInteractions() {
        return (views != null ? views : 0)
               + (downloadCount != null ? downloadCount : 0)
               + (listenCount != null ? listenCount : 0);
    }
}
