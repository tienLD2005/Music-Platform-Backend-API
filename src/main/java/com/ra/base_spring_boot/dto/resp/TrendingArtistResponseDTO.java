package com.ra.base_spring_boot.dto.resp;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrendingArtistResponseDTO {
    private Long id;
    private String fullName;
    private String profileImage;
    private String bio;
    private long totalListens;
    private long totalDownloads;
}