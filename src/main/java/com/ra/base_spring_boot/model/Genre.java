package com.ra.base_spring_boot.model;

import com.ra.base_spring_boot.model.base.BaseObject;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "genres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre extends BaseObject {
    @Column(name = "genre_name", length = 255, nullable = false)
    private String genreName;

    @ManyToMany(mappedBy = "genres")
    private Set<Song> songs;

}
