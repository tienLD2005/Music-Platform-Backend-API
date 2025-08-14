package com.ra.base_spring_boot.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumFilter {
    private Long artistId;
    private int page;
    private int size;
    private String keyword;
    private String sortDir;
    private boolean isPremium;
}
