package com.ra.base_spring_boot.model;

import com.ra.base_spring_boot.model.base.BaseObject;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "follows")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Follow extends BaseObject {

    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private User artist;
}
