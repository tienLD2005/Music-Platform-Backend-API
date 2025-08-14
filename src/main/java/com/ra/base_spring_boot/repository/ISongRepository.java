package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ISongRepository extends JpaRepository<Song, Long> {
    Page<Song> findByAlbumId(Long albumId, Pageable pageable);

    @Query("SELECT s FROM Song s JOIN s.genres g WHERE g.id = :genreId")
    Page<Song> findByGenreId(@Param("genreId") Long genreId, Pageable pageable);

}
