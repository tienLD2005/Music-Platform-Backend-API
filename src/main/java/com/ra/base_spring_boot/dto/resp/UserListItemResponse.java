package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListItemResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String status;
    private Set<String> roles;

    public static UserListItemResponse fromEntity(User user) {
        return UserListItemResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .roles(user.getRoles() != null
                        ? user.getRoles().stream()
                        .map(role -> role.getRoleName().name())
                        .collect(Collectors.toSet())
                        : Collections.emptySet())
                .build();
    }
}
