package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.dto.resp.TopSongDTO;
import com.ra.base_spring_boot.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface ISongRepository extends JpaRepository<Song, Long> {
    Page<Song> findByAlbumId(Long albumId, Pageable pageable);

    @Query("SELECT s FROM Song s JOIN s.genres g WHERE g.id = :genreId")
    Page<Song> findByGenreId(@Param("genreId") Long genreId, Pageable pageable);

    @Query("""
    SELECT new com.ra.base_spring_boot.dto.resp.TopSongDTO(
        s.id,
        s.title,
        s.duration,
        s.fileUrl,
        s.views,
        COUNT(d)
    )
    FROM Song s
    LEFT JOIN Download d\s
        ON s.id = d.song.id\s
        AND d.addedAt >= :startDate
    WHERE s.createdAt >= :startDate
    GROUP BY s.id, s.title, s.duration, s.fileUrl, s.views
    ORDER BY (s.views + COUNT(d)) DESC
""")
    List<TopSongDTO> findTopSongsOfWeek(@Param("startDate") LocalDateTime startDate, Pageable pageable);

}
