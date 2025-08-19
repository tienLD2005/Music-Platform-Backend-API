package com.ra.base_spring_boot.mapper;

import com.ra.base_spring_boot.dto.resp.SongReactionResponseDto;
import com.ra.base_spring_boot.model.SongReaction;
import com.ra.base_spring_boot.model.constants.ReactionEnum;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SongReactionMapper {

    public SongReactionResponseDto toResponseDto(SongReaction songReaction) {
        if (songReaction == null) {
            return null;
        }

        return SongReactionResponseDto.builder()
                .id(songReaction.getId())
                .userId(songReaction.getUser() != null ? songReaction.getUser().getId() : null)
                .userName(songReaction.getUser() != null ? songReaction.getUser().getFirstName() + " " + songReaction.getUser().getLastName(): null)
                .songId(songReaction.getSong() != null ? songReaction.getSong().getId() : null)
                .songTitle(songReaction.getSong() != null ? songReaction.getSong().getTitle() : null)
                .reaction(songReaction.getReaction())
                .createdAt(songReaction.getCreatedAt())
                .updatedAt(songReaction.getUpdatedAt())
                .build();
    }

    public List<SongReactionResponseDto> toResponseDtoList(List<SongReaction> songReactions) {
        if (songReactions == null) {
            return null;
        }

        return songReactions.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public Map<String, Long> toStringKeyMap(Map<ReactionEnum, Long> reactionCounts) {
        if (reactionCounts == null) {
            return null;
        }

        return reactionCounts.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().name(),
                        Map.Entry::getValue
                ));
    }
}
