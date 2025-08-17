package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<User, Long> {

    @Query("""
        SELECT COUNT(DISTINCT u)
        FROM User u
        JOIN u.roles r
        WHERE r.roleName = 'ROLE_ARTIST'
    """)
    long countArtists();

    @Query("""
        SELECT u.id, CONCAT(u.firstName, ' ', u.lastName), COUNT(a)
        FROM User u
        JOIN u.roles r
        JOIN u.albums a
        WHERE r.roleName = 'ROLE_ARTIST'
        GROUP BY u.id, u.firstName, u.lastName
        ORDER BY COUNT(a) DESC
    """)
    List<Object[]> findArtistWithMostAlbums(Pageable pageable);

    @Query("""
        SELECT u.id, CONCAT(u.firstName, ' ', u.lastName), COUNT(s)
        FROM User u
        JOIN u.roles r
        JOIN u.songs s
        WHERE r.roleName = 'ROLE_ARTIST'
        GROUP BY u.id, u.firstName, u.lastName
        ORDER BY COUNT(s) DESC
    """)
    List<Object[]> findArtistWithMostSongs(Pageable pageable);

    @Query("""
        SELECT u.id, CONCAT(u.firstName, ' ', u.lastName), COUNT(sh)
        FROM User u
        JOIN u.roles r
        JOIN u.songs s
        LEFT JOIN s.songHistories sh
        WHERE r.roleName = 'ROLE_ARTIST'
        GROUP BY u.id, u.firstName, u.lastName
        ORDER BY COUNT(sh) DESC
    """)
    List<Object[]> findTopArtistsByPlays(Pageable pageable);
}
