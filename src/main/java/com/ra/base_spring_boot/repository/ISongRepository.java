package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.dto.resp.GenreStatDTO;
import com.ra.base_spring_boot.dto.resp.SongResponse;
import com.ra.base_spring_boot.dto.resp.TopSongDTO;
import com.ra.base_spring_boot.dto.resp.TopSongOfWeek;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.constants.SongStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface ISongRepository extends JpaRepository<Song, Long> {
    Page<Song> findByAlbumIdAndStatus(Long albumId, Pageable pageable, SongStatus status);

    @Query("SELECT s FROM Song s JOIN s.genres g WHERE g.id = :genreId")
    Page<Song> findByGenreId(@Param("genreId") Long genreId, Pageable pageable);

    boolean existsByTitleAndAlbumId(String title, Long albumId);

    @Query("""
    SELECT new com.ra.base_spring_boot.dto.resp.TopSongOfWeek(
        s.id,
        s.title,
        s.duration,
        s.fileUrl,
        CAST(s.views AS long),
        CAST(COALESCE(SUM(CASE WHEN d.addedAt >= :startDate THEN 1 ELSE 0 END), 0) AS long),
        CAST(COALESCE(SUM(CASE WHEN sh.playedAt >= :startDate THEN 1 ELSE 0 END), 0) AS long)
    )
    FROM Song s
    LEFT JOIN Download d ON d.song = s
    LEFT JOIN SongHistory sh ON sh.song = s
    GROUP BY s.id, s.title, s.duration, s.fileUrl, s.views
    ORDER BY (s.views +
              COALESCE(SUM(CASE WHEN d.addedAt >= :startDate THEN 1 ELSE 0 END), 0) +
              COALESCE(SUM(CASE WHEN sh.playedAt >= :startDate THEN 1 ELSE 0 END), 0)) DESC
""")
    List<TopSongOfWeek> findTopSongsOfWeek(@Param("startDate") LocalDateTime startDate, Pageable pageable);


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

    Page<Song> findByTitle(String keyword, Pageable pageable);

    @Query("SELECT g.genreName FROM Song s JOIN s.genres g WHERE s.id = :songId")
    List<String> findGenresBySongId(@Param("songId") Long songId);

    @Modifying
    @Query("UPDATE Song s SET s.views = s.views + 1 WHERE s.id = :songId")
    void incrementViews(@Param("songId") Long songId);

    @Query("SELECT g.genreName, COUNT(s) " +
            "FROM Song s JOIN s.genres g " +
            "GROUP BY g.genreName" +
            " ORDER BY COUNT(s) ASC")
    List<Object[]> countSongsByGenre();

    @Query("SELECT g.genreName, COUNT(sh) " +
            "FROM SongHistory sh " +
            "JOIN sh.song s " +
            "JOIN s.genres g " +
            "GROUP BY g.genreName " +
            "ORDER BY COUNT(sh) DESC")
    List<Object[]> countPlaysByGenre();

    @Query("""
        SELECT new com.ra.base_spring_boot.dto.resp.TopSongDTO(
            s.id, s.title, s.duration, s.fileUrl, s.views,
            COUNT(DISTINCT d)
        )
        FROM Song s
        LEFT JOIN s.downloads d
        JOIN s.songHistories sh
        WHERE sh.playedAt BETWEEN :startDate AND :endDate
        GROUP BY s.id, s.title, s.duration, s.fileUrl, s.views
        ORDER BY COUNT(sh) DESC
        """)
    List<TopSongDTO> findTrendingSongs(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("select sh.song.id from SongHistory sh where sh.user.id = :userId")
    List<Long> findListenedSongIds(@Param("userId") Long userId);

    @Query("""
    select new com.ra.base_spring_boot.dto.resp.GenreStatDTO(
        g.id, g.genreName, count(sh.id)
    )
    from SongHistory sh
    join sh.song s
    join s.genres g
    where sh.user.id = :userId
    group by g.id, g.genreName
    order by count(sh.id) desc
""")
    List<GenreStatDTO> countGenresByUser(@Param("userId") Long userId);

    @Query("""
    SELECT new com.ra.base_spring_boot.dto.resp.SongResponse(
        s.id,
        s.title,
        s.duration,
        s.artist.fullName,
        s.artist.id,
        s.album.title,
        s.album.id,
        s.fileUrl,
        s.views,
        s.createdAt,
        s.status
    )
    FROM Song s
    JOIN s.genres g
    WHERE g.id IN :topGenreIds
      AND s.id NOT IN :listenedSongIds
""")
    Page<SongResponse> findRecommendedSongs(
            @Param("topGenreIds") List<Long> topGenreIds,
            @Param("listenedSongIds") List<Long> listenedSongIds,
            Pageable pageable
    );
}
