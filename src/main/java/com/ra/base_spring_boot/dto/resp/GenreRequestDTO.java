package com.ra.base_spring_boot.dto.resp;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreRequestDTO {
    @NotBlank(message = "Genre name is required")
    private String name;
    private String description;
}