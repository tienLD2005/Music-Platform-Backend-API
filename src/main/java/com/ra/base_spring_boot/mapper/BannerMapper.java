package com.ra.base_spring_boot.mapper;

import com.ra.base_spring_boot.dto.resp.BannerResponse;
import com.ra.base_spring_boot.model.Banner;

public class BannerMapper {

    public static BannerResponse toBannerResponse(Banner banner) {
        return BannerResponse.builder()
                .id(banner.getId())
                .title(banner.getTitle())
                .imageUrl(banner.getImageUrl())
                .position(banner.getPosition())
                .startTime(banner.getStartTime())
                .endTime(banner.getEndTime())
                .status(banner.getStatus())
                .createdAt(banner.getCreatedAt())
                .build();
    }
}
