package com.ra.base_spring_boot.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishlistResponse {
    private Long id;
    private String title;
    private String artistName;
    private String albumName;
    private String fileUrl;
    private Integer views;
    private LocalTime duration;
}
