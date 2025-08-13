package com.ra.base_spring_boot.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@Data
public class FormRegister {
    @NotBlank(message = "Cannot be blank")
    private String lastName;

    @NotBlank(message = "Cannot be blank")
    private String firstName;

    @NotBlank(message = "Cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character"
    )
    private String password;

    @NotBlank(message = "Confirm password cannot be blank")
    private String confirmPassword;

    private Set<String> role;

    public FormRegister(String fullName, String email, String password, String confirmPassword) {
        this.lastName = fullName != null ? fullName.trim() : null;
        this.firstName = email != null ? email.trim() : null;
        this.email = email != null ? email.trim() : null;
        this.password = password != null ? password.trim() : null;
        this.confirmPassword = confirmPassword != null ? confirmPassword.trim() : null;
    }
}
