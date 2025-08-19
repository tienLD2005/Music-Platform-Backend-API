package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.constants.ReactionEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongReactionResponseDto {
    private Long id;
    private Long userId;
    private String userName;
    private Long songId;
    private String songTitle;
    private ReactionEnum reaction;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
