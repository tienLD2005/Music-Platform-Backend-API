package com.ra.base_spring_boot.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DownloadSongRequest {
    @NotNull(message = "Song ID không được để trống")
    private Long songId;
}
