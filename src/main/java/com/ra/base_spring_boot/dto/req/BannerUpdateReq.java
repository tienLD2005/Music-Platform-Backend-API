package com.ra.base_spring_boot.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ra.base_spring_boot.model.constants.BannerStatus;
import jakarta.validation.constraints.Future;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class BannerUpdateReq {
    private String title;
    private String position;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Future(message = "The end time must be in the future.")
    private LocalDateTime endTime;

    private BannerStatus status;
    private MultipartFile image;
}
