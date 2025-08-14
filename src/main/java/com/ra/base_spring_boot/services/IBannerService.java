package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.BannerCreateRequest;
import com.ra.base_spring_boot.dto.req.BannerUpdateReq;
import com.ra.base_spring_boot.dto.resp.BannerResponseDTO;
import org.springframework.data.domain.Page;

public interface IBannerService {
    Page<BannerResponseDTO> getAll(int page, int size);
    Page<BannerResponseDTO> searchByKeyword(int page, int size, String keyword);
    BannerResponseDTO create(BannerCreateRequest req);
    BannerResponseDTO update(Integer id, BannerUpdateReq req);
    void delete(Integer id);
}
