package com.ra.base_spring_boot.dto.req;

import com.ra.base_spring_boot.model.constants.ReactionEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ReactionRequestDTO {
    @NotBlank(message = "Reaction required")
    @Pattern(
            regexp = "LIKE|LOVE|SAD|ANGRY|CLAP",
            message = "Reaction must be one of: LIKE, LOVE, SAD, ANGRY, CLAP"
    )
    private String reactionEnum;

}
