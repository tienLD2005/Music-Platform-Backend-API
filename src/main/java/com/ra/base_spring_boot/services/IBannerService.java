package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.BannerCreateReq;
import com.ra.base_spring_boot.dto.resp.BannerRes;
import org.springframework.data.domain.Page;

public interface IBannerService {
    Page<BannerRes> getAll(int page, int size, String keyword);
    BannerRes create(BannerCreateReq req);
    void delete(Integer id);
}
