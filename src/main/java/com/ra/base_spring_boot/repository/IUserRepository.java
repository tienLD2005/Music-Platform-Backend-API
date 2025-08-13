package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long>
{
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationCode(String code);
    Optional<User> findByResetPasswordCode(String code);

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
