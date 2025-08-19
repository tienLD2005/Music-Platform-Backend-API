package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.SongReaction;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.ReactionEnum;
import com.ra.base_spring_boot.repository.ISongReactionRepository;
import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.ISongReactionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SongReactionServiceImpl implements ISongReactionService {

    private final ISongReactionRepository songReactionRepository;
    private final ISongRepository songRepository;
    private final IUserRepository userRepository;

    @Override
    public SongReaction reactToSong(Long songId, Long userId, ReactionEnum reaction) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new EntityNotFoundException("Song not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Optional<SongReaction> existing = songReactionRepository.findByUserAndSong(user, song);

        if (existing.isPresent()) {
            SongReaction sr = existing.get();
            sr.setReaction(reaction);
            return songReactionRepository.save(sr);
        } else {
            SongReaction sr = SongReaction.builder()
                    .song(song)
                    .user(user)
                    .reaction(reaction)
                    .build();
            return songReactionRepository.save(sr);
        }
    }

    @Override
    public void removeReaction(Long songId, Long userId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new EntityNotFoundException("Song not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        songReactionRepository.findByUserAndSong(user, song)
                .ifPresent(songReactionRepository::delete);
    }

    @Override
    public ReactionEnum getUserReaction(Long songId, Long userId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new EntityNotFoundException("Song not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return songReactionRepository.findByUserAndSong(user, song)
                .map(SongReaction::getReaction)
                .orElse(null);
    }

    @Override
    public Map<ReactionEnum, Long> getReactionsCount(Long songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new EntityNotFoundException("Song not found"));

        Map<ReactionEnum, Long> counts = new EnumMap<>(ReactionEnum.class);
        for (ReactionEnum type : ReactionEnum.values()) {
            counts.put(type, songReactionRepository.countBySongAndReaction(song, type));
        }
        return counts;
    }
}
