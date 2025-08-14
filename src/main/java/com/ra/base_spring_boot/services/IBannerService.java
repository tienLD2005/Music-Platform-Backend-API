package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.BannerCreateRequest;
import com.ra.base_spring_boot.dto.resp.BannerResponseDTO;
import com.ra.base_spring_boot.dto.resp.BannerResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IBannerService {
    Page<BannerResponseDTO> getAll(int page, int size, String keyword);
    BannerResponseDTO create(BannerCreateRequest req);
    void delete(Integer id);
    List<BannerResponse> getActiveBanners(String position);
}
