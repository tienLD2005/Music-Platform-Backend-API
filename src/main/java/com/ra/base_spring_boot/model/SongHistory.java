package com.ra.base_spring_boot.model;

import com.ra.base_spring_boot.model.base.SongHistoryId;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "song_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SongHistory {

    @EmbeddedId
    private SongHistoryId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("songId")
    @JoinColumn(name = "song_id")
    private Song song;

    @CreationTimestamp
    @Column(name = "played_at")
    private LocalDateTime playedAt;
}


