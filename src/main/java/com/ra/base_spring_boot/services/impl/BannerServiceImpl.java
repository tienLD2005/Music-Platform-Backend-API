package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.BannerCreateRequest;
import com.ra.base_spring_boot.dto.req.BannerUpdateReq;
import com.ra.base_spring_boot.dto.resp.BannerResponseDTO;
import com.ra.base_spring_boot.model.Banner;
import com.ra.base_spring_boot.model.constants.BannerStatus;
import com.ra.base_spring_boot.repository.IBannerRepository;
import com.ra.base_spring_boot.services.IBannerService;
import com.ra.base_spring_boot.services.cloudinary.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements IBannerService {

    private final IBannerRepository bannerRepository;
    private final CloudinaryService cloudinaryService;

    private void validateTime(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && !start.isBefore(end)) {
            throw new IllegalArgumentException("startTime phải nhỏ hơn endTime");
        }
    }

    @Override
    public Page<BannerResponseDTO> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Banner> banners = bannerRepository.findByStatus(BannerStatus.ACTIVE, pageable);
        return banners.map(this::toRes);
    }

    @Override
    public Page<BannerResponseDTO> searchByKeyword(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Banner> banners = bannerRepository.findByStatusAndTitleContainingIgnoreCase(
                BannerStatus.ACTIVE,
                keyword == null ? "" : keyword,
                pageable
        );
        return banners.map(this::toRes);
    }

    @Override
    public BannerResponseDTO create(BannerCreateRequest req) {
        validateTime(req.getStartTime(), req.getEndTime());

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
    public BannerResponseDTO update(Integer id, BannerUpdateReq req) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Banner không tồn tại"));

        validateTime(req.getStartTime(), req.getEndTime());

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

        return toRes(bannerRepository.save(banner));
    }


    @Override
    public void delete(Integer id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("The banner does not exist"));
        banner.setStatus(BannerStatus.INACTIVE);
        bannerRepository.save(banner);
    }

    private BannerResponseDTO toRes(Banner banner) {
        return BannerResponseDTO.builder()
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
