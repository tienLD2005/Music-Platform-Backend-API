package com.ra.base_spring_boot.dto.req;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LyricsRequest {
    private Long songId;
}