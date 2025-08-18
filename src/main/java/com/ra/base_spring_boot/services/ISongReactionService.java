package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.model.SongReaction;
import com.ra.base_spring_boot.model.constants.ReactionEnum;

import java.util.Map;

public interface ISongReactionService {
    SongReaction reactToSong(Long songId, Long userId, ReactionEnum reaction);

    void removeReaction(Long songId, Long userId);

    ReactionEnum getUserReaction(Long songId, Long userId);

    Map<ReactionEnum, Long> getReactionsCount(Long songId);
}
