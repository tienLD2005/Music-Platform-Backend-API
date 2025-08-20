package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IPlaylistRepository extends JpaRepository<Playlist, Long> {

    @Query("""
        SELECT p 
        FROM Playlist p 
        WHERE p.user.id = :userId 
          AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    Page<Playlist> findByUserIdAndKeyword(@Param("userId") Long userId,
                                          @Param("keyword") String keyword,
                                          Pageable pageable);
}
