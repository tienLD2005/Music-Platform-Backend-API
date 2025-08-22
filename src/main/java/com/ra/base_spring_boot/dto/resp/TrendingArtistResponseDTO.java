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
    private Long totalInteractions;

    public TrendingArtistResponseDTO(Long id, String fullName, String profileImage, String bio,
                                     Long songHistoryCount, Long songReactionCount, Long downloadCount) {
        this.id = id;
        this.fullName = fullName;
        this.profileImage = profileImage;
        this.bio = bio;
        this.totalInteractions = songHistoryCount + songReactionCount + downloadCount;
    }
}