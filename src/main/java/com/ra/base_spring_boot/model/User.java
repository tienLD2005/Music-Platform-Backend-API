package com.ra.base_spring_boot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ra.base_spring_boot.model.base.BaseObject;
import com.ra.base_spring_boot.model.constants.AuthProvider;
import com.ra.base_spring_boot.model.constants.UStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User extends BaseObject {

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "profile_image", length = 255)
    private String profileImage;

    @Lob
    @Column(name = "bio")
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private UStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "reset_password_code")
    private String resetPasswordCode;

    @Column(name = "reset_password_expiration")
    private LocalDateTime resetPasswordExpiration;

    @Column(name = "verification_expiration")
    private LocalDateTime verificationExpiration;

    @Column(name = "account_expiration")
    private LocalDateTime accountExpiration;

    @Column(name = "provider", length = 20)
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Column(name = "provider_id", length = 100)
    private String providerId;

    @Column(name = "last_password_change_at")
    private LocalDateTime lastPasswordChangeAt;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @ManyToMany
    @JoinTable(
            name = "wishlists",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    private Set<Song> wishlistSongs;

    @OneToMany(mappedBy = "user")
    private List<Download> downloads;

    @OneToMany(mappedBy = "user")
    private List<SongHistory> songHistories;

    @OneToMany(mappedBy = "user")
    private List<Payment> payments;

    @OneToMany(mappedBy = "user")
    private List<Subscription> subscriptions;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Album> albums;

    @OneToMany(mappedBy = "artist")
    private List<Song> songs;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SongReaction> songReactions;
}
