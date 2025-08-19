package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.constants.ReactionEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReactionResponseDTO {
    private Long id;
    private Long userId;
    private Long commentId;
    private ReactionEnum reactionEnum;
}
