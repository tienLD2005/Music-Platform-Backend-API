package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Role;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IUserRepository extends JpaRepository<User, Long>
{
    Optional<User> findByEmail(String email);

    @Query("""
    SELECT DISTINCT u
    FROM User u
    JOIN u.roles r
    WHERE r.roleName = com.ra.base_spring_boot.model.constants.RoleName.ROLE_ARTIST
    """)
    Page<User> findAllArtists(Pageable pageable);

    @Query("""
    SELECT u
    FROM User u
    JOIN u.roles r
    LEFT JOIN u.songs s
    WHERE r.roleName = com.ra.base_spring_boot.model.constants.RoleName.ROLE_ARTIST
    GROUP BY u
    ORDER BY SUM(COALESCE(s.views, 0)) DESC
    """)
    Page<User> findTrendingArtists(Pageable pageable);
}
