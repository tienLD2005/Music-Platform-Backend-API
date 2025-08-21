package com.ra.base_spring_boot.repository;


import com.ra.base_spring_boot.model.SongDeleteHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongDeleteHistoryRepo extends JpaRepository<SongDeleteHistory, Long> {
}
