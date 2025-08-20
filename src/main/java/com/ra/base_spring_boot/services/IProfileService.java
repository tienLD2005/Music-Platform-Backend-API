package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.ChangePasswordRequest;
import com.ra.base_spring_boot.dto.req.UpdateProfileRequest;
import com.ra.base_spring_boot.dto.resp.UserProfileResponseDTO;

public interface IProfileService {
    UserProfileResponseDTO getProfile(Long userId);
    UserProfileResponseDTO updateProfile(Long userId, UpdateProfileRequest request);
    void changePassword(Long userId, ChangePasswordRequest request);
}
