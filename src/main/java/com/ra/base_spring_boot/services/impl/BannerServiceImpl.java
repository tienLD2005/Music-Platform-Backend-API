package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.BannerCreateReq;
import com.ra.base_spring_boot.dto.req.BannerUpdateReq;
import com.ra.base_spring_boot.dto.resp.BannerRes;
import com.ra.base_spring_boot.model.Banner;
import com.ra.base_spring_boot.model.constants.BannerStatus;
import com.ra.base_spring_boot.repository.IBannerRepository;
import com.ra.base_spring_boot.services.IBannerService;
import com.ra.base_spring_boot.services.cloudinary.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements IBannerService {

    private final IBannerRepository bannerRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public Page<BannerRes> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Banner> banners = bannerRepository.findByStatus(BannerStatus.ACTIVE, pageable);
        return banners.map(this::toRes);
    }

    @Override
    public Page<BannerRes> searchByKeyword(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Banner> banners = bannerRepository.findByStatusAndTitleContainingIgnoreCase(
                BannerStatus.ACTIVE,
                keyword == null ? "" : keyword,
                pageable
        );
        return banners.map(this::toRes);
    }

    @Override
    public BannerRes create(BannerCreateReq req) {
        try {
            String imageUrl = cloudinaryService.uploadImage(req.getImage());
            Banner banner = Banner.builder()
                    .title(req.getTitle())
                    .position(req.getPosition())
                    .startTime(req.getStartTime())
                    .endTime(req.getEndTime())
                    .status(BannerStatus.ACTIVE)
                    .imageUrl(imageUrl)
                    .build();
            return toRes(bannerRepository.save(banner));
        } catch (Exception e) {
            throw new RuntimeException("Error uploading image: " + e.getMessage());
        }
    }

    @Override
    public BannerRes update(Integer id, BannerUpdateReq req) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Banner không tồn tại"));

        if (req.getImage() != null && !req.getImage().isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadImage(req.getImage());
                banner.setImageUrl(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi upload ảnh: " + e.getMessage());
            }
        }

        if (req.getTitle() != null) banner.setTitle(req.getTitle());
        if (req.getPosition() != null) banner.setPosition(req.getPosition());
        if (req.getStartTime() != null) banner.setStartTime(req.getStartTime());
        if (req.getEndTime() != null) banner.setEndTime(req.getEndTime());
        if (req.getStatus() != null) banner.setStatus(req.getStatus());

        Banner saved = bannerRepository.save(banner);
        return toRes(saved);
    }


    @Override
    public void delete(Integer id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("The banner does not exist"));
        banner.setStatus(BannerStatus.INACTIVE);
        bannerRepository.save(banner);
    }

    private BannerRes toRes(Banner banner) {
        return BannerRes.builder()
                .id(banner.getId())
                .title(banner.getTitle())
                .position(banner.getPosition())
                .imageUrl(banner.getImageUrl())
                .startTime(banner.getStartTime())
                .endTime(banner.getEndTime())
                .status(banner.getStatus())
                .build();
    }
}
