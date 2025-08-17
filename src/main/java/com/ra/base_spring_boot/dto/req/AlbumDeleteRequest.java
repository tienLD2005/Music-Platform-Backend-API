package com.ra.base_spring_boot.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumDeleteRequest {
    @NotNull(message = "Album ID không được để trống")
    private Long albumId;

    @NotBlank(message = "Lý do xóa không được để trống")
    private String reason;

    private String additionalNotes;
}
