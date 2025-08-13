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
    @NotBlank(message = "Không được để trống")
    private String lastName;

    @NotBlank(message = "Không được để trống")
    private String firstName;

    @NotBlank(message = "Không được để trống")
    @Email
    private String email;

    @NotBlank(message = "Không được để trống")
    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Mật khẩu phải chứa ít nhất 1 chữ hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt"
    )
    private String password;

    @NotBlank(message = "Không được để trống xác nhận mật khẩu")
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
