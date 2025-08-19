package com.ra.base_spring_boot.dto.resp;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LyricsResponseDTO {
    private Long id;
    private String content;
    private String sourceUrl;
    private Long songId;
}
