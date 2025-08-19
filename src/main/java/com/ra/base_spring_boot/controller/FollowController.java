package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.FollowResponseDTO;
import com.ra.base_spring_boot.security.principle.MyUserDetails;
import com.ra.base_spring_boot.services.IFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/follows")
@RequiredArgsConstructor
public class FollowController {

    private final IFollowService followService;

    @PostMapping("/artists/{artistId}")
    public ResponseEntity<ResponseWrapper<FollowResponseDTO>> followArtist(
            @PathVariable Long artistId,
            Authentication authentication) {
        Long followerId = getCurrentUserId(authentication);
        FollowResponseDTO response = followService.followArtist(followerId, artistId);
        return buildResponse(response);
    }

    @DeleteMapping("/artists/{artistId}")
    public ResponseEntity<ResponseWrapper<String>> unfollowArtist(
            @PathVariable Long artistId,
            Authentication authentication) {
        Long followerId = getCurrentUserId(authentication);
        followService.unfollowArtist(followerId, artistId);
        return buildResponse("Unfollow successful");
    }

    @GetMapping("/me/artists")
    public ResponseEntity<ResponseWrapper<List<FollowResponseDTO>>> getFollowedArtists(
            Authentication authentication) {
        Long followerId = getCurrentUserId(authentication);
        List<FollowResponseDTO> response = followService.getFollowedArtists(followerId);
        return buildResponse(response);
    }

    @GetMapping("/artists/{artistId}/followers")
    public ResponseEntity<ResponseWrapper<List<FollowResponseDTO>>> getArtistFollowers(
            @PathVariable Long artistId,
            Authentication authentication
    ) {
        Long currentUserId = getCurrentUserId(authentication);
        List<FollowResponseDTO> response = followService.getArtistFollowers(artistId, currentUserId);
        return buildResponse(response);
    }

    @GetMapping("/artists/{artistId}/followers/count")
    public ResponseEntity<ResponseWrapper<Long>> getFollowerCount(
            @PathVariable Long artistId,
            Authentication authentication
    ) {
        Long currentUserId = getCurrentUserId(authentication);
        long count = followService.getFollowerCount(artistId, currentUserId);
        return buildResponse(count);
    }


    private Long getCurrentUserId(Authentication authentication) {
        return ((MyUserDetails) authentication.getPrincipal()).getId();
    }

    private <T> ResponseEntity<ResponseWrapper<T>> buildResponse(T data) {
        return ResponseEntity.ok(
                ResponseWrapper.<T>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(data)
                        .build()
        );
    }
}
