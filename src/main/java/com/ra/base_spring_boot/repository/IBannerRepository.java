package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Banner;
import com.ra.base_spring_boot.model.constants.BannerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface IBannerRepository extends JpaRepository<Banner, Integer> {
    Page<Banner> findByStatus(BannerStatus status, Pageable pageable);
    @Query("""
        SELECT b FROM Banner b
        WHERE b.status = :status
          AND b.startTime <= :currentTime
          AND b.endTime >= :currentTime
          AND (:position IS NULL OR b.position = :position)
        ORDER BY b.createdAt DESC
    """)
    List<Banner> findActiveBanners(@Param("status") BannerStatus status,
                                   @Param("currentTime") LocalDateTime currentTime,
                                   @Param("position") String position);
    Page<Banner> findByStatusAndTitleContainingIgnoreCase(
            BannerStatus status,
            String keyword,
            Pageable pageable
    );
}
