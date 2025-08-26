package com.ra.base_spring_boot.dto.resp;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String profileImage;
    private String bio;
    private Set<String> roles;
}
