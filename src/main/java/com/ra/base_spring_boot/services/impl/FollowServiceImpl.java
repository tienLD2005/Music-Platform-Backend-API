package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.resp.FollowResponseDTO;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpForbidden;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.mapper.FollowMapper;
import com.ra.base_spring_boot.model.Follow;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.RoleName;
import com.ra.base_spring_boot.repository.FollowRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.IFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements IFollowService {

    private final FollowRepository followRepository;
    private final IUserRepository userRepository;

    @Override
    @Transactional
    public FollowResponseDTO followArtist(Long followerId, Long artistId) {
        if (followerId.equals(artistId)) {
            throw new HttpBadRequest("You cannot follow yourself");
        }

        User follower = getUserById(followerId, "Follower not found with id " + followerId);
        User artist = getUserById(artistId, "Artist not found with id " + artistId);

        boolean isUser = follower.getRoles().stream()
                .anyMatch(role -> role.getRoleName() == RoleName.ROLE_USER);

        if (!isUser) {
            throw new HttpBadRequest("Only USER accounts can follow artists");
        }

        boolean isArtist = artist.getRoles().stream()
                .anyMatch(role -> role.getRoleName() == RoleName.ROLE_ARTIST);

        if (!isArtist) {
            throw new HttpBadRequest("You can only follow ARTIST accounts");
        }

        boolean alreadyFollowed = followRepository.findByFollowerAndArtist(follower, artist).isPresent();
        if (alreadyFollowed) {
            throw new HttpBadRequest("Already following this artist");
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .artist(artist)
                .build();

        followRepository.save(follow);

        return FollowMapper.toDTO(follow);
    }

    @Override
    @Transactional
    public void unfollowArtist(Long followerId, Long artistId) {
        User follower = getUserById(followerId, "Follower not found with id " + followerId);
        User artist = getUserById(artistId, "Artist not found with id " + artistId);

        Follow follow = followRepository.findByFollowerAndArtist(follower, artist)
                .orElseThrow(() -> new HttpNotFound("Follow relation not found"));

        followRepository.delete(follow);
    }

    @Override
    public List<FollowResponseDTO> getFollowedArtists(Long followerId) {
        User follower = getUserById(followerId, "User not found with id " + followerId);

        return followRepository.findByFollower(follower).stream()
                .map(FollowMapper::toDTO)
                .toList();
    }

    @Override
    public List<FollowResponseDTO> getArtistFollowers(Long artistId, Long currentUserId) {
        User artist = getUserById(artistId, "Artist not found with id " + artistId);

        if (!artist.getId().equals(currentUserId)) {
            throw new HttpForbidden("You cannot view other artists' followers");
        }

        return followRepository.findByArtist(artist).stream()
                .map(FollowMapper::toDTO)
                .toList();
    }

    @Override
    public long getFollowerCount(Long artistId, Long currentUserId) {
        User artist = getUserById(artistId, "Artist not found with id " + artistId);

        if (!artist.getId().equals(currentUserId)) {
            throw new HttpForbidden("You cannot view other artists' followers");
        }

        return followRepository.countByArtist(artist);
    }

    private User getUserById(Long id, String errorMessage) {
        return userRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound(errorMessage));
    }
}
