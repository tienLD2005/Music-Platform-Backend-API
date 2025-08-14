package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Banner;
import com.ra.base_spring_boot.model.constants.BannerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBannerRepository extends JpaRepository<Banner, Integer> {
    Page<Banner> findByStatus(BannerStatus status, Pageable pageable);
}
