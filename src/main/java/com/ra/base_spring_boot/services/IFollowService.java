package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.resp.FollowResponseDTO;

import java.util.List;

public interface IFollowService {
    FollowResponseDTO followArtist(Long followerId, Long artistId);
    void unfollowArtist(Long followerId, Long artistId);
    List<FollowResponseDTO> getFollowedArtists(Long followerId);
    List<FollowResponseDTO> getArtistFollowers(Long artistId);
    long getFollowerCount(Long artistId);
}
