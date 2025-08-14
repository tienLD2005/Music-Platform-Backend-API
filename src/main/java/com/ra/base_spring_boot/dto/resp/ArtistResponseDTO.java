package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistResponseDTO{
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String profileImage;
    private String bio;
    private Long totalViews;

    public static ArtistResponseDTO fromEntity(User user) {
        return ArtistResponseDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .bio(user.getBio())
                .totalViews(
                        user.getSongs() != null
                                ? user.getSongs().stream().mapToLong(Song::getViews).sum()
                                : 0L
                )
                .build();
    }

}
