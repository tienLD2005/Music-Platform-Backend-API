package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Album;
import com.ra.base_spring_boot.model.constants.AlbumStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.time.LocalDateTime;

@Repository
public interface IAlbumRepository extends JpaRepository<Album, Long> {
    @Query("""
        SELECT a 
        FROM Album a 
        WHERE a.artist.id = :artistId
        AND (:title IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%')))
        """)
    Page<Album> findByArtistAndTitle(Long artistId, String title, Pageable pageable);

    @Query("""
        SELECT COUNT(s) 
        FROM Song s 
        WHERE s.album.id = :albumId
        """)
    Long countSongsInAlbum(Long albumId);

    boolean existsByTitleIgnoreCaseAndArtistId(String title, Long artistId);

    @Query("""
    SELECT a,\s
           SUM(COALESCE(s.views, 0)) + COUNT(sh)
    FROM Album a
    LEFT JOIN a.songs s
    LEFT JOIN s.songHistories sh
    GROUP BY a.id
    ORDER BY SUM(COALESCE(s.views, 0)) + COUNT(sh) DESC
""")
    List<Object[]> findTopTrendingAlbumsWithViews(Pageable pageable);


    //List Album
    Page<Album> findByTitleContainingIgnoreCaseOrArtist_LastNameContainingIgnoreCase(String title, String artistName, Pageable pageable);

    @Query("""
        SELECT s.album 
        FROM Song s 
        WHERE s.album.status = :status 
          AND s.createdAt >= :fromDate
        GROUP BY s.album 
        ORDER BY SUM(COALESCE(s.views, 0)) DESC
    """)
    Page<Album> findTopAlbumsByViewsSince(@Param("status")AlbumStatus status,
                                          @Param("fromDate") LocalDateTime fromDate,
                                          Pageable pageable);


    // GET ALBUM TRENDING
    @Query("SELECT a FROM Album a " +
            "JOIN a.artist u " +
            "JOIN Song s ON s.album = a " +
            "GROUP BY a.id " +
            "ORDER BY SUM(s.views) DESC")
    Page<Album> findFeaturedAlbums(Pageable pageable);

    //GET ALBUM BY ARTIST
    @Query("SELECT a FROM Album a " +
            "WHERE a.artist.id = :artistId " +
            "AND (:keyword IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:isPremium = false OR a.type = com.ra.base_spring_boot.model.constants.AlbumType.PREMIUM)")
    Page<Album> findAlbumsByArtist(@Param("artistId") Long artistId,
                                   @Param("keyword") String keyword,
                                   @Param("isPremium") boolean isPremium,
                                   Pageable pageable);


    @Query("SELECT COUNT(a) FROM Album a")
    Long countTotalAlbums();

    @Query("""
        SELECT a.id, a.title, COUNT(sh) as playCount
        FROM Album a
        JOIN a.songs s
        LEFT JOIN SongHistory sh ON sh.song = s
        GROUP BY a.id, a.title
        ORDER BY playCount DESC
    """)
    List<Object[]> findMostPlayedAlbums();

    @Query("""
        SELECT ar.lastName, COUNT(a)
        FROM Album a
        JOIN a.artist ar
        GROUP BY ar.id, ar.lastName
    """)
    List<Object[]> countAlbumsByArtist();

    @Query("""
        SELECT YEAR(a.releaseDate), COUNT(a)
        FROM Album a
        GROUP BY YEAR(a.releaseDate)
        ORDER BY YEAR(a.releaseDate)
    """)
    List<Object[]> countAlbumsByYear();

    @Query("""
        SELECT a.status, COUNT(a)
        FROM Album a
        GROUP BY a.status
    """)
    List<Object[]> countAlbumsByStatus();

}
