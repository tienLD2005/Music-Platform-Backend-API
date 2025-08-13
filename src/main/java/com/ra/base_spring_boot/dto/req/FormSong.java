package com.ra.base_spring_boot.dto.req;

import jakarta.validation.constraints.Min;
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
public class FormSong {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;
    @NotNull(message = "Thể loại không được để trống")
    private LocalTime duration;
    private MultipartFile fileUrl;
    @Min(value = 0, message = "Số lượt nghe không được nhỏ hơn 0")
    private Integer views;
    private Long albumId;
    private Set<Long> genreIds;
}
