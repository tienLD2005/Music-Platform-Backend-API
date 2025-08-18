package com.ra.base_spring_boot.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ra.base_spring_boot.model.constants.BannerStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class BannerCreateRequest {
    @NotBlank(message = "The title must not be empty.")
    private String title;

    private String position;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @NotNull(message = "The start time cannot be left blank.")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @NotNull(message = "The end time must not be left blank.")
    @Future(message = "The end time must be in the future.")
    private LocalDateTime endTime;

    private BannerStatus status;

    @NotNull(message = "The image cannot be empty.")
    private MultipartFile image;
}
