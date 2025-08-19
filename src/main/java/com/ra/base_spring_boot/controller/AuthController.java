package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.*;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.UStatus;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.IAuthService;
import com.ra.base_spring_boot.services.email.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;
    private final IUserRepository userRepository;
    private final EmailService emailService;

    @PostMapping("/login")
    public ResponseEntity<?> handleLogin(@Valid @RequestBody FormLoginRequest formLogin) {
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(authService.login(formLogin))
                        .build()
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> handleRegister(@Valid @RequestBody FormRegisterRequest formRegister) {
        authService.register(formRegister);
        return ResponseEntity.created(URI.create("/api/v1/auth/register"))
                .body(ResponseWrapper.builder()
                        .status(HttpStatus.CREATED)
                        .code(201)
                        .data("Registration successful! Please check your email to verify your account.")
                        .build()
                );
    }

    @GetMapping("/verify")
    public ResponseEntity<?> handleVerifyEmail(@RequestParam String code) {
        authService.verifyEmail(code);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Email verified successfully! You can now log in.")
                        .build()
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> handleForgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("OTP code has been sent to your email.")
                        .build()
        );
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<?> handleResetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Password has been reset successfully.")
                        .build()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> handleLogout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Logged out successfully.")
                        .build()
        );
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestParam String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new HttpBadRequest("User not found"));

        if (user.getStatus() == UStatus.ACTIVE) {
            throw new HttpBadRequest("Account already verified");
        }

        String newCode = UUID.randomUUID().toString();
        user.setVerificationCode(newCode);
        userRepository.save(user);

        emailService.sendEmail(user.getEmail(), "Resend Account Verification",
                "Click the link to verify your account: http://localhost:8080/api/v1/auth/verify?code=" + newCode);

        return ResponseEntity.ok("Verification email resent successfully");
    }

}