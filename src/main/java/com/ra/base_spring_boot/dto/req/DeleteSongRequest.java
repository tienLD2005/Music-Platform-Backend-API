package com.ra.base_spring_boot.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteSongRequest {
    @NotBlank(message = "Reason must not be empty")
    private String reason;
}