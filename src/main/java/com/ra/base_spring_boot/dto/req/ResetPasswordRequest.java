package com.ra.base_spring_boot.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "Cannot be blank")
    private String code;
    @NotBlank(message = "Cannot be blank")
    private String newPassword;
}
