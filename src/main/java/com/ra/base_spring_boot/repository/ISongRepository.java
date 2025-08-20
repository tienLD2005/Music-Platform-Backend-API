package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.dto.req.SongStatisticsFilterRequestDTO;
import com.ra.base_spring_boot.dto.resp.SongResponse;
import com.ra.base_spring_boot.dto.resp.SongStatisticsResponseDTO;
import com.ra.base_spring_boot.dto.resp.TopSongDTO;
import com.ra.base_spring_boot.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


public interface ISongRepository extends JpaRepository<Song, Long> {
    Page<Song> findByAlbumId(Long albumId, Pageable pageable);

    @Query("SELECT s FROM Song s JOIN s.genres g WHERE g.id = :genreId")
    Page<Song> findByGenreId(@Param("genreId") Long genreId, Pageable pageable);

    // CHECK DUPLICATE SONG TITLE
    boolean existsByTitleAndAlbumId(String title, Long albumId);

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

    @Query("SELECT COUNT(s) FROM Song s")
    long countTotalSongs();

    @Query("""
        SELECT s.id, s.title, COUNT(sh) as playCount
        FROM Song s
        LEFT JOIN SongHistory sh ON sh.song = s
        LEFT JOIN s.genres g
        WHERE (:artistId IS NULL OR s.artist.id = :artistId)
          AND (:genreId IS NULL OR g.id = :genreId)
          AND (:albumId IS NULL OR s.album.id = :albumId)
        GROUP BY s.id, s.title, s.createdAt
        ORDER BY
            CASE WHEN :sortBy = 'plays' THEN COUNT(sh) END DESC,
            CASE WHEN :sortBy = 'release' THEN s.createdAt END DESC
    """)
    List<Object[]> getPlayCountBySong(
            @Param("artistId") Long artistId,
            @Param("genreId") Long genreId,
            @Param("albumId") Long albumId,
            @Param("sortBy") String sortBy
    );

    @Query("""
        SELECT s.id, s.title, COUNT(w) as wishlistCount
        FROM Song s
        LEFT JOIN s.usersWishlist w
        LEFT JOIN s.genres g
        WHERE (:artistId IS NULL OR s.artist.id = :artistId)
          AND (:genreId IS NULL OR g.id = :genreId)
          AND (:albumId IS NULL OR s.album.id = :albumId)
        GROUP BY s.id, s.title
        ORDER BY COUNT(w) DESC
    """)
    List<Object[]> getTopFavoriteSongsFiltered(
            @Param("artistId") Long artistId,
            @Param("genreId") Long genreId,
            @Param("albumId") Long albumId
    );

    @Query("""
    SELECT new com.ra.base_spring_boot.dto.resp.TopSongDTO(
        s.id, s.title, s.duration, s.fileUrl, s.views, COUNT(d)
    )
    FROM Song s
    LEFT JOIN Download d
        ON s.id = d.song.id
    GROUP BY s.id, s.title, s.duration, s.fileUrl, s.views
    ORDER BY s.views DESC
""")
    List<TopSongDTO> findTopSongsAllTime(Pageable pageable);

    @Query("""
    SELECT new com.ra.base_spring_boot.dto.resp.TopSongDTO(
        s.id,
        s.title,
        s.duration,
        s.fileUrl,
        s.views,
        COUNT(DISTINCT d)
    )
    FROM SongHistory sh
    JOIN sh.song s
    LEFT JOIN Download d ON s.id = d.song.id
    WHERE sh.playedAt >= :startDate
    GROUP BY s.id, s.title, s.duration, s.fileUrl, s.views
    ORDER BY COUNT(sh) DESC
""")
    List<TopSongDTO> findTrendingSongs(@Param("startDate") LocalDateTime startDate, Pageable pageable);



    Page<Song> findByTitle(String keyword, Pageable pageable);

    @Query("SELECT g.genreName FROM Song s JOIN s.genres g WHERE s.id = :songId")
    List<String> findGenresBySongId(@Param("songId") Long songId);




}
