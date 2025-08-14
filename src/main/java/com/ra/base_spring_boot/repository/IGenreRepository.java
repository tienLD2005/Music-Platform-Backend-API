package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.dto.resp.GenreTrendingDTO;
import com.ra.base_spring_boot.model.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface IGenreRepository extends JpaRepository<Genre, Long>{
    Page<Genre> findAll(Pageable pageable);

    @Query("""
    SELECT new com.ra.base_spring_boot.dto.resp.GenreTrendingDTO(
        g.id,
        g.genreName,
        COUNT(sh.id)
    )
    FROM Song s
    JOIN s.genres g
    JOIN s.songHistories sh
    WHERE sh.playedAt BETWEEN :startDate AND :endDate
    GROUP BY g.id, g.genreName
    ORDER BY COUNT(sh.id) DESC
""")
    List<GenreTrendingDTO> findTopGenres(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
