package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.AlbumAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IAlbumAuditLogRepository extends JpaRepository<AlbumAuditLog, Long> {

    @Query("SELECT a FROM AlbumAuditLog a WHERE a.albumId = :albumId ORDER BY a.createdAt DESC")
    Page<AlbumAuditLog> findByAlbumIdOrderByCreatedAtDesc(@Param("albumId") Long albumId, Pageable pageable);

    @Query("SELECT a FROM AlbumAuditLog a WHERE a.action = :action ORDER BY a.createdAt DESC")
    Page<AlbumAuditLog> findByActionOrderByCreatedAtDesc(@Param("action") String action, Pageable pageable);
}
