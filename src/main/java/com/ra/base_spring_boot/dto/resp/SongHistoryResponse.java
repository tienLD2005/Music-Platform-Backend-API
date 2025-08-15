package com.ra.base_spring_boot.dto.resp;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongHistoryResponse {
    private Long songId;
    private String title;
    private String artistName;
    private String coverImage;
    private String fileUrl;
    private LocalDateTime playedAt;
}
