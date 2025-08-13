package com.ra.base_spring_boot.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Builder
public class FormLogin {
    @NotBlank(message = "Không được để trống")
    @Email
    private String email;

    @NotBlank(message = "Không được để trống")
    private String password;

    public FormLogin(String email, String password) {
        this.email = email != null ? email.trim() : null;
        this.password = password != null ? password.trim() : null;
    }
}
