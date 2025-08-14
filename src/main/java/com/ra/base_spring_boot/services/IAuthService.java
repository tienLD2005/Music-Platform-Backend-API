package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.ForgotPasswordRequest;
import com.ra.base_spring_boot.dto.req.FormLoginRequest;
import com.ra.base_spring_boot.dto.req.FormRegisterRequest;
import com.ra.base_spring_boot.dto.req.ResetPasswordRequest;
import com.ra.base_spring_boot.dto.resp.JwtResponse;

public interface IAuthService
{

    void register(FormRegisterRequest formRegister);

    JwtResponse login(FormLoginRequest formLogin);

    void verifyEmail(String code);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void logout(String rawToken);
}
