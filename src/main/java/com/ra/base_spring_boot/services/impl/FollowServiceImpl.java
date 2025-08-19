package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.resp.FollowResponseDTO;
import com.ra.base_spring_boot.exception.BadRequestException;
import com.ra.base_spring_boot.exception.ResourceNotFoundException;
import com.ra.base_spring_boot.model.Follow;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.repository.FollowRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.IFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements IFollowService {

    private final FollowRepository followRepository;
    private final IUserRepository userRepository;

    @Override
    public FollowResponseDTO followArtist(Long followerId, Long artistId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));

        if (followRepository.findByFollowerAndArtist(follower, artist).isPresent()) {
            throw new BadRequestException("Already following this artist");
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .artist(artist)
                .build();
        followRepository.save(follow);

        return FollowResponseDTO.builder()
                .artistId(artistId)
                .artistName(getFullName(artist))
                .message("Follow successful")
                .build();
    }

    @Override
    public void unfollowArtist(Long followerId, Long artistId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));

        Follow follow = followRepository.findByFollowerAndArtist(follower, artist)
                .orElseThrow(() -> new ResourceNotFoundException("Follow relation not found"));

        followRepository.delete(follow);
    }

    @Override
    public List<FollowResponseDTO> getFollowedArtists(Long followerId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return followRepository.findByFollower(follower).stream()
                .map(f -> FollowResponseDTO.builder()
                        .artistId(f.getArtist().getId())
                        .artistName(getFullName(f.getArtist()))
                        .message("Following")
                        .build())
                .toList();
    }

    @Override
    public List<FollowResponseDTO> getArtistFollowers(Long artistId) {
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));

        return followRepository.findByArtist(artist).stream()
                .map(f -> FollowResponseDTO.builder()
                        .artistId(artistId)
                        .artistName(getFullName(artist))
                        .message("Follower: " + getFullName(f.getFollower()))
                        .build())
                .toList();
    }

    @Override
    public long getFollowerCount(Long artistId) {
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));
        return followRepository.countByArtist(artist);
    }

    private String getFullName(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }
}
