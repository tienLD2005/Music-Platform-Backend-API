package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.PlaylistSong;
import com.ra.base_spring_boot.model.base.PlaylistSongId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, PlaylistSongId> {
}
