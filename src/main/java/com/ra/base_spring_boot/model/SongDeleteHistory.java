package com.ra.base_spring_boot.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "song_delete_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongDeleteHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long songId;
    private String songTitle;
    private Long artistId;
    private String reason;

    private LocalDateTime deletedAt;

    private String deletedBy;

    private String deleteReason;

}
