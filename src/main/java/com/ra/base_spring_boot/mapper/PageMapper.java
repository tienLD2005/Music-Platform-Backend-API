package com.ra.base_spring_boot.mapper;

import com.ra.base_spring_boot.dto.resp.PageResponse;
import org.springframework.data.domain.Page;

public class PageMapper {

    public static <T> PageResponse<T> toPageResponse(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .size(page.getSize())
                .build();
    }
}

