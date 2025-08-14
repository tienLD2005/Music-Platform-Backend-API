package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.Song;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseGenre {
    private Long id;
    private String genreName;
}
