package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.ChangePasswordRequest;
import com.ra.base_spring_boot.dto.req.UpdateProfileRequest;
import com.ra.base_spring_boot.dto.resp.UserProfileResponseDTO;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.AuthProvider;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.IProfileService;
import com.ra.base_spring_boot.services.cloudinary.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements IProfileService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    @Override
    public UserProfileResponseDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HttpNotFound("User not found"));

        return new UserProfileResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getProfileImage(),
                user.getBio(),
                user.getRoles().stream().map(r -> r.getRoleName().name()).collect(Collectors.toSet())
        );
    }

    @Override
    public UserProfileResponseDTO updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HttpNotFound("User not found"));

        if (StringUtils.hasText(request.getFirstName())) {
            user.setFirstName(request.getFirstName().trim().replaceAll("\\s+", " "));
        }
        if (StringUtils.hasText(request.getLastName())) {
            user.setLastName(request.getLastName().trim().replaceAll("\\s+", " "));
        }
        if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(request.getProfileImage());
            user.setProfileImage(imageUrl);
        }
        if (StringUtils.hasText(request.getBio())) {
            user.setBio(request.getBio().trim().replaceAll("\\s+", " "));
        }

        userRepository.save(user);
        return getProfile(user.getId());
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HttpNotFound("User not found"));

        AuthProvider provider = user.getProvider() == null ? AuthProvider.LOCAL : user.getProvider();

        if (provider == AuthProvider.LOCAL) {
            validatePasswordRequest(request, user);
        } else {
            validateNewPasswordOnly(request);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setLastPasswordChangeAt(LocalDateTime.now());
        userRepository.save(user);
    }


    private void validatePasswordRequest(ChangePasswordRequest request, User user) {
        if (!StringUtils.hasText(request.getOldPassword())) {
            throw new HttpBadRequest("Old password must not be empty");
        }
        if (!StringUtils.hasText(request.getNewPassword())) {
            throw new HttpBadRequest("New password must not be empty");
        }
        if (!StringUtils.hasText(request.getConfirmPassword())) {
            throw new HttpBadRequest("Password confirmation must not be empty");
        }

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new HttpBadRequest("Old password is incorrect");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new HttpBadRequest("New password must be different from old password");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new HttpBadRequest("Password confirmation does not match");
        }
        if (request.getNewPassword().length() < 8) {
            throw new HttpBadRequest("Password must be at least 8 characters long");
        }
    }
    private void validateNewPasswordOnly(ChangePasswordRequest request) {
        if (!StringUtils.hasText(request.getNewPassword())) {
            throw new HttpBadRequest("New password must not be empty");
        }
        if (!StringUtils.hasText(request.getConfirmPassword())) {
            throw new HttpBadRequest("Password confirmation must not be empty");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new HttpBadRequest("Password confirmation does not match");
        }
        if (request.getNewPassword().length() < 8) {
            throw new HttpBadRequest("Password must be at least 8 characters long");
        }
    }

}
