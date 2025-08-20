package com.ra.base_spring_boot.mapper;

import com.ra.base_spring_boot.dto.resp.FollowResponseDTO;
import com.ra.base_spring_boot.model.Follow;
import com.ra.base_spring_boot.model.User;

public class FollowMapper {

    public static FollowResponseDTO toDTO(Follow follow) {
        return FollowResponseDTO.builder()
                .artistId(follow.getArtist().getId())
                .artistName(getFullName(follow.getArtist()))
                .followerId(follow.getFollower().getId())
                .followerName(getFullName(follow.getFollower()))
                .message("Follow success")
                .build();
    }

    private static String getFullName(User user) {
        if (user == null) {
            return null;
        }
        return user.getFirstName() + " " + user.getLastName();
    }
}
