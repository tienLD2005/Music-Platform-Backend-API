package com.ra.base_spring_boot.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FormSongRequest {
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotNull(message = "Duration cannot be blank")
    @Schema(type = "string", pattern = "HH:mm:ss", example = "00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime duration;
    private MultipartFile fileUrl;
    private Set<Long> genreIds;
}
