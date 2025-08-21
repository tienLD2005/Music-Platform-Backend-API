package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Download;
import com.ra.base_spring_boot.model.base.DownloadId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IDownloadSongRepository extends JpaRepository<Download, DownloadId> {

    @Query("SELECT d FROM Download d WHERE d.user.id = :userId")
    Page<Download> findByUserId(@Param("userId") Long userId, Pageable pageable);

    boolean existsByUserIdAndSongId(Long userId, Long songId);

    Optional<Download> findByUserIdAndSongId(Long userId, Long songId);

    long countByUserId(Long userId);

    @Query("SELECT d FROM Download d WHERE d.user.id = :userId ORDER BY d.addedAt DESC")
    List<Download> findByUserIdOrderByDownloadedAtDesc(@Param("userId") Long userId);

    @Query("SELECT d FROM Download d JOIN d.song s WHERE d.user.id = :userId ORDER BY s.title ASC")
    List<Download> findByUserIdOrderBySongTitleAsc(@Param("userId") Long userId);
}
