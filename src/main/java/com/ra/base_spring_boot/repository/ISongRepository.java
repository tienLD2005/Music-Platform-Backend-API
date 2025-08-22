package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.dto.resp.TopSongDTO;
import com.ra.base_spring_boot.dto.resp.TopSongOfWeek;
import com.ra.base_spring_boot.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface ISongRepository extends JpaRepository<Song, Long> {
    Page<Song> findByAlbumId(Long albumId, Pageable pageable);

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

    @Query(value = """
        SELECT 
            s.id AS songId,
            s.title AS title,
            u.id AS artistName,
            COUNT(DISTINCT CONCAT(sh.user_id, '_', sh.song_id)) AS playCount,
            COUNT(DISTINCT sh.user_id) AS uniqueListeners,
            SUM(CASE WHEN sr.reaction IN ('LIKE','LOVE','CLAP') THEN 1 ELSE 0 END) AS positiveReactions,
            SUM(CASE WHEN sr.reaction IN ('SAD','ANGRY','REPORT') THEN 1 ELSE 0 END) AS negativeReactions,
            COUNT(DISTINCT d.user_id) AS downloadCount,
            COUNT(DISTINCT ps.playlist_id) AS playlistAddCount,
            COUNT(DISTINCT c.id) AS commentCount
        FROM songs s
        JOIN users u ON u.id = s.artist_id
        LEFT JOIN song_histories sh ON sh.song_id = s.id AND sh.played_at >= :fromTime
        LEFT JOIN song_reactions sr ON sr.song_id = s.id
        LEFT JOIN downloads d ON d.song_id = s.id
        LEFT JOIN playlist_song ps ON ps.song_id = s.id
        LEFT JOIN comments c ON c.song_id = s.id
        GROUP BY s.id, s.title, u.id
        ORDER BY playCount DESC
        LIMIT :limit
    """, nativeQuery = true)
    List<Object[]> findTrendingSongs(@Param("fromTime") LocalDateTime fromTime, @Param("limit") int limit);



    Page<Song> findByTitle(String keyword, Pageable pageable);

    @Query("SELECT g.genreName FROM Song s JOIN s.genres g WHERE s.id = :songId")
    List<String> findGenresBySongId(@Param("songId") Long songId);

    @Modifying
    @Query("UPDATE Song s SET s.views = s.views + 1 WHERE s.id = :songId")
    void incrementViews(@Param("songId") Long songId);

    @Query("SELECT g.genreName, COUNT(s) " +
            "FROM Song s JOIN s.genres g " +
            "GROUP BY g.genreName")
    List<Object[]> countSongsByGenre();

    @Query("SELECT g.genreName, COUNT(sh) " +
            "FROM SongHistory sh " +
            "JOIN sh.song s " +
            "JOIN s.genres g " +
            "GROUP BY g.genreName " +
            "ORDER BY COUNT(sh) DESC")
    List<Object[]> countPlaysByGenre();

}
