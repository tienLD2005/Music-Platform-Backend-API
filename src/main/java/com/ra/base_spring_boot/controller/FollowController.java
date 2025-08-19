package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.FollowResponseDTO;
import com.ra.base_spring_boot.services.IFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/follows")
@RequiredArgsConstructor
public class FollowController {

    private final IFollowService followService;

    @PostMapping("/{followerId}/follow/{artistId}")
    public ResponseWrapper<FollowResponseDTO> followArtist(@PathVariable Long followerId,
                                                           @PathVariable Long artistId) {
        FollowResponseDTO response = followService.followArtist(followerId, artistId);
        return ResponseWrapper.<FollowResponseDTO>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data(response)
                .build();
    }

    @DeleteMapping("/{followerId}/unfollow/{artistId}")
    public ResponseEntity<ResponseWrapper<String>> unfollowArtist(@PathVariable Long followerId,
                                                                  @PathVariable Long artistId) {
        followService.unfollowArtist(followerId, artistId);
        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data("Unfollow successful")
                        .build()
        );
    }

    @GetMapping("/{followerId}/artists")
    public ResponseWrapper<List<FollowResponseDTO>> getFollowedArtists(@PathVariable Long followerId) {
        List<FollowResponseDTO> response = followService.getFollowedArtists(followerId);
        return ResponseWrapper.<List<FollowResponseDTO>>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data(response)
                .build();
    }

    @GetMapping("/{artistId}/followers")
    public ResponseWrapper<List<FollowResponseDTO>> getArtistFollowers(@PathVariable Long artistId) {
        List<FollowResponseDTO> response = followService.getArtistFollowers(artistId);
        return ResponseWrapper.<List<FollowResponseDTO>>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data(response)
                .build();
    }

    @GetMapping("/artists/{artistId}/followers/count")
    public ResponseWrapper<Long> getFollowerCount(@PathVariable Long artistId) {
        long count = followService.getFollowerCount(artistId);
        return ResponseWrapper.<Long>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data(count)
                .build();
    }
}
