package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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


}
