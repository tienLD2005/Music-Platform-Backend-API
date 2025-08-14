package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.BannerCreateReq;
import com.ra.base_spring_boot.dto.resp.BannerRes;
import com.ra.base_spring_boot.model.Banner;
import com.ra.base_spring_boot.model.constants.BannerStatus;
import com.ra.base_spring_boot.repository.IBannerRepository;
import com.ra.base_spring_boot.services.CloudinaryService;
import com.ra.base_spring_boot.services.IBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements IBannerService {

    private final IBannerRepository bannerRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public Page<BannerRes> getAll(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Banner> banners = bannerRepository.findByStatus(BannerStatus.ACTIVE, pageable);

        if (keyword != null && !keyword.isBlank()) {
            banners = new PageImpl<>(
                    banners.stream()
                            .filter(b -> b.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                            .toList(),
                    pageable,
                    banners.getTotalElements()
            );
        }
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
