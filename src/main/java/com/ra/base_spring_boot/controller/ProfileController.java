package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.ChangePasswordRequest;
import com.ra.base_spring_boot.dto.req.UpdateProfileRequest;
import com.ra.base_spring_boot.dto.resp.UserProfileResponseDTO;
import com.ra.base_spring_boot.security.principle.MyUserDetails;
import com.ra.base_spring_boot.services.IProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final IProfileService profileService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<UserProfileResponseDTO>> getProfile(
            @AuthenticationPrincipal MyUserDetails userDetails) {
        UserProfileResponseDTO profile = profileService.getProfile(userDetails.getId());
        return ResponseEntity.ok(
                ResponseWrapper.<UserProfileResponseDTO>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(profile)
                        .build()
        );
    }

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseWrapper<UserProfileResponseDTO>> updateProfile(
            @AuthenticationPrincipal MyUserDetails userDetails,
            @ModelAttribute UpdateProfileRequest request) {

        UserProfileResponseDTO updated = profileService.updateProfile(userDetails.getId(), request);

        return ResponseEntity.ok(
                ResponseWrapper.<UserProfileResponseDTO>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(updated)
                        .build()
        );
    }

    @PatchMapping("/change-password")
    public ResponseEntity<ResponseWrapper<String>> changePassword(
            @AuthenticationPrincipal MyUserDetails userDetails,
            @RequestBody @Valid ChangePasswordRequest request) {
        profileService.changePassword(userDetails.getId(), request);
        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data("Password changed successfully")
                        .build()
        );
    }
}
