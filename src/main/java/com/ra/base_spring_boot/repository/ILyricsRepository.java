package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Lyrics;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ILyricsRepository extends JpaRepository<Lyrics, Long> {
    Lyrics findBySongId(Long songId);

    @Transactional
    @Modifying
    @Query("delete from Lyrics l where l.song.id = :songId")
    void deleteAllBySongId(@Param("songId") Long songId);
}
