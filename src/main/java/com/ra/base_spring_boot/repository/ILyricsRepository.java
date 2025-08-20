package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Lyrics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ILyricsRepository extends JpaRepository<Lyrics, Long> {
    Lyrics findBySongId(Long songId);
}
