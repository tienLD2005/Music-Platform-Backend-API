package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlbumRepository extends JpaRepository<Album, Long> {

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
}
