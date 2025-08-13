package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.ForgotPasswordRequest;
import com.ra.base_spring_boot.dto.req.FormLogin;
import com.ra.base_spring_boot.dto.req.FormRegister;
import com.ra.base_spring_boot.dto.req.ResetPasswordRequest;
import com.ra.base_spring_boot.dto.resp.JwtResponse;
import com.ra.base_spring_boot.dto.resp.UserResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.model.BlacklistedToken;
import com.ra.base_spring_boot.model.Role;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.RoleName;
import com.ra.base_spring_boot.model.constants.UStatus;
import com.ra.base_spring_boot.repository.IBlacklistedTokenRepository;
import com.ra.base_spring_boot.repository.IRoleRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.security.principle.MyUserDetails;
import com.ra.base_spring_boot.services.IAuthService;
import com.ra.base_spring_boot.services.IRoleService;
import com.ra.base_spring_boot.services.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final IRoleService roleService;
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final EmailService emailService;
    private final IBlacklistedTokenRepository  blacklistedTokenRepository;
    private final IRoleRepository roleRepository;

    @Override
    public void register(FormRegister request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Password and confirm password do not match");
        }
        Set<Role> roles = request.getRole().stream()
                .map(roleName -> roleRepository.findByRoleName(RoleName.valueOf(roleName))
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        String code = UUID.randomUUID().toString();
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .status(UStatus.VERIFY)
                .verificationCode(code)
                .roles(roles)
                .build();

        userRepository.save(user);

        emailService.sendEmail(request.getEmail(), "Account Verification",
                "Click the link to verify your account: http://localhost:8080/api/v1/auth/verify?code=" + code);
    }


    @Override
    public JwtResponse login(FormLogin formLogin) {
        Optional<User> optionalUser = userRepository.findByEmail(formLogin.getEmail());
        if (optionalUser.isEmpty()) {
            throw new HttpBadRequest("Email does not exist");
        }

        User user = optionalUser.get();

        switch (user.getStatus()) {
            case VERIFY -> throw new HttpBadRequest("Account is not activated");
            case BLOCKED -> throw new HttpBadRequest("Account is blocked");
            case ACTIVE -> {}
            default -> throw new HttpBadRequest("Invalid account status");
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(formLogin.getEmail(), formLogin.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new HttpBadRequest("Incorrect password");
        }

        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();

        UserResponse userDto = UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .bio(user.getBio())
                .status(user.getStatus().name())
                .createdAt(user.getCreatedAt().toString())
                .updatedAt(user.getUpdatedAt().toString())
                .build();

        return JwtResponse.builder()
                .accessToken(jwtProvider.generateToken(userDetails.getUsername()))
                .user(userDto)
                .roles(userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    public void verifyEmail(String code) {
        User user = userRepository.findByVerificationCode(code)
                .orElseThrow(() -> new RuntimeException("Invalid verification code"));
        user.setStatus(UStatus.ACTIVE);
        user.setVerificationCode(null);
        userRepository.save(user);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email does not exist"));
        String code = UUID.randomUUID().toString().substring(0, 6);
        user.setResetPasswordCode(code);
        userRepository.save(user);

        emailService.sendEmail(user.getEmail(), "Password Reset Code", "Your OTP code is: " + code);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetPasswordCode(request.getCode())
                .orElseThrow(() -> new RuntimeException("Invalid OTP code"));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordCode(null);
        userRepository.save(user);
    }

    @Override
    public void logout(String rawToken) {
        if (rawToken == null || !rawToken.startsWith("Bearer ")) {
            throw new HttpBadRequest("Invalid token");
        }

        String token = rawToken.substring(7);

        Date expiryDate = jwtProvider.extractExpiration(token);

        BlacklistedToken blacklisted = BlacklistedToken.builder()
                .token(token)
                .expiryDate(expiryDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime())
                .build();
        blacklistedTokenRepository.save(blacklisted);
    }


}
