package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.dto.resp.GenreTrendingDTO;
import com.ra.base_spring_boot.model.Genre;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface IGenreRepository extends JpaRepository<Genre, Long>{
    @Override
    @NonNull
    Page<Genre> findAll(@NonNull Pageable pageable);

    @Query("""
    SELECT new com.ra.base_spring_boot.dto.resp.GenreTrendingDTO(
        g.id,
        g.genreName,
        COUNT(sh.id)
    )
    FROM Genre g
    LEFT JOIN g.songs s
    LEFT JOIN s.songHistories sh
    WHERE sh.playedAt BETWEEN :startDate AND :endDate OR sh.id IS NULL
    GROUP BY g.id, g.genreName
    ORDER BY COUNT(sh.id) DESC
    """)
    Page<GenreTrendingDTO> findTopGenres(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("""
        SELECT g
        FROM Genre g
        WHERE (:keyword IS NULL OR LOWER(g.genreName) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    List<Genre> findGenresByName(String keyword);


    @Query("""
        SELECT g
        FROM Genre g
        LEFT JOIN g.songs s
        WHERE (:keyword IS NULL OR LOWER(g.genreName) LIKE LOWER(CONCAT('%', :keyword, '%')))
        GROUP BY g
        ORDER BY COUNT(s) DESC
    """)
    List<Genre> findGenresOrderBySongCountDesc(String keyword);


    @Query("""
        SELECT g
        FROM Genre g
        LEFT JOIN g.songs s
        WHERE (:keyword IS NULL OR LOWER(g.genreName) LIKE LOWER(CONCAT('%', :keyword, '%')))
        GROUP BY g
        ORDER BY COUNT(s) ASC
    """)
    List<Genre> findGenresOrderBySongCountAsc(String keyword);


}
