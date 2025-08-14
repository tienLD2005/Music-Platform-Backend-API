package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ISongRepository extends JpaRepository<Song, Long> {
    Page<Song> findByAlbumId(Long albumId, Pageable pageable);
}
