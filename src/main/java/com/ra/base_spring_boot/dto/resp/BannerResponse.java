package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.constants.BannerStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BannerResponse {
    private Integer id;
    private String title;
    private String imageUrl;
    private String position;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BannerStatus status;
    private LocalDateTime createdAt;
}
