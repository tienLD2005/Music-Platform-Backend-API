package com.ra.base_spring_boot.dto.req;

import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.AlbumType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AlbumRequest {

    @NotBlank(message = "Tên Album không được để trống")
    private String title;

    @NotBlank(message = "Ngày phát được để trống")
    private LocalDateTime releaseDate;

    // Khóa ngoại tới bảng User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private User artist;

    @Column(name = "cover_image", length = 255)
    private String coverImage;

    @Enumerated(EnumType.STRING)
    private AlbumType type;
}
