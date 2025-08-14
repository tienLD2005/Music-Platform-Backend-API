package com.ra.base_spring_boot.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Builder
public class FormLoginRequest {
    @NotBlank(message = "Cannot be blank")
    @Email
    private String email;

    @NotBlank(message = "Cannot be blank")
    private String password;

    public FormLoginRequest(String email, String password) {
        this.email = email != null ? email.trim() : null;
        this.password = password != null ? password.trim() : null;
    }
}
