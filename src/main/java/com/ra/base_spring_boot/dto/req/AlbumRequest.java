package com.ra.base_spring_boot.dto.req;

import com.ra.base_spring_boot.model.constants.AlbumType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AlbumRequest {

    @NotBlank(message = "Album name must not be empty")
    private String title;

    @NotNull(message = "Release date must not be empty")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime releaseDate;

    @NotNull(message = "Type must not be empty")
    private AlbumType type;

    private MultipartFile coverImageFile;
}
