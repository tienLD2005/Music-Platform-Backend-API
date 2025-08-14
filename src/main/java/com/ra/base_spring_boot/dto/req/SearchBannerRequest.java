package com.ra.base_spring_boot.dto.req;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class SearchBannerRequest {

    @Min(value = 0, message = "Trang bắt đầu từ 0")
    private Integer page = 0;

    @Min(value = 1, message = "Kích thước trang tối thiểu là 1")
    private Integer size = 10;

    private String keyword;
}
