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

    @Override
    public void register(FormRegister request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã tồn tại");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu và xác nhận mật khẩu không khớp");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findByRoleName(RoleName.ROLE_USER));

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

        emailService.sendEmail(request.getEmail(), "Xác thực tài khoản",
                "Nhấn vào link để xác thực: http://localhost:8080/api/v1/auth/verify?code=" + code);
    }


    @Override
    public JwtResponse login(FormLogin formLogin) {
        Optional<User> optionalUser = userRepository.findByEmail(formLogin.getEmail());
        if (optionalUser.isEmpty()) {
            throw new HttpBadRequest("Email không tồn tại");
        }

        User user = optionalUser.get();

        switch (user.getStatus()) {
            case VERIFY -> throw new HttpBadRequest("Tài khoản chưa được kích hoạt");
            case BLOCKED -> throw new HttpBadRequest("Tài khoản đã bị khóa");
            case ACTIVE -> {}
            default -> throw new HttpBadRequest("Trạng thái tài khoản không hợp lệ");
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(formLogin.getEmail(), formLogin.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new HttpBadRequest("Mật khẩu không chính xác");
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
                .orElseThrow(() -> new RuntimeException("Mã xác thực không hợp lệ"));
        user.setStatus(UStatus.ACTIVE);
        user.setVerificationCode(null);
        userRepository.save(user);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));
        String code = UUID.randomUUID().toString().substring(0, 6);
        user.setResetPasswordCode(code);
        userRepository.save(user);

        emailService.sendEmail(user.getEmail(), "Mã đặt lại mật khẩu", "Mã OTP của bạn: " + code);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetPasswordCode(request.getCode())
                .orElseThrow(() -> new RuntimeException("Mã OTP không hợp lệ"));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordCode(null);
        userRepository.save(user);
    }

    @Override
    public void logout(String rawToken) {
        if (rawToken == null || !rawToken.startsWith("Bearer ")) {
            throw new HttpBadRequest("Token không hợp lệ");
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
