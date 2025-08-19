package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.SongReaction;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.ReactionEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ISongReactionRepository extends JpaRepository<SongReaction, Long> {

    Optional<SongReaction> findByUserAndSong(User user, Song song);

    List<SongReaction> findBySong(Song song);

    long countBySongAndReaction(Song song, ReactionEnum reaction);
}
