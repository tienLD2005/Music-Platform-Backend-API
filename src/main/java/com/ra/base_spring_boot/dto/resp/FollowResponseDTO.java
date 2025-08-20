package com.ra.base_spring_boot.dto.resp;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FollowResponseDTO {
    private Long artistId;
    private String artistName;
    private String message;
    private Long followerId;
    private String followerName;
}
