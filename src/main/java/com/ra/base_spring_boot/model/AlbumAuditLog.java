package com.ra.base_spring_boot.model;

import com.ra.base_spring_boot.model.base.BaseObject;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "album_audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumAuditLog extends BaseObject {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @Column(name = "album_id", nullable = false)
    private Long albumId;

    @Column(name = "album_title", nullable = false, length = 255)
    private String albumTitle;

    @Column(name = "artist_email", nullable = false, length = 255)
    private String artistEmail;

    @Column(name = "action", nullable = false, length = 50)
    private String action; // "DELETE", "APPROVE", "REJECT"

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "additional_notes", columnDefinition = "TEXT")
    private String additionalNotes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
