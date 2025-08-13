package com.ra.base_spring_boot.dto.req;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String code;
    private String newPassword;
}
