package com.ra.base_spring_boot.model;

import com.ra.base_spring_boot.model.base.BaseObject;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "songs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Song extends BaseObject {

    @Column(length = 255, nullable = false)
    private String title;

    @Column
    private LocalTime duration;

    // Nghệ sĩ (tham chiếu tới bảng users)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private User artist;

    // Album (tham chiếu tới bảng albums)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    @Column(name = "file_url", length = 255)
    private String fileUrl;

    @Column
    private Integer views;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(
            name = "song_genre",
            joinColumns = @JoinColumn(name = "song_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres;

    @ManyToMany(mappedBy = "wishlistSongs")
    private Set<User> usersWishlist;

    @OneToMany(mappedBy = "song")
    private List<Download> downloads;

    @OneToMany(mappedBy = "song")
    private List<PlaylistSong> playlistSongs;

    @OneToMany(mappedBy = "song")
    private List<SongHistory> songHistories;
}