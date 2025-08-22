package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.dto.resp.TrendingArtistResponseDTO;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.UStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long>
{
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationCode(String code);
    Optional<User> findByResetPasswordCode(String code);
    Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName, String lastName, String email, Pageable pageable);

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

    @Query("""
        SELECT u
        FROM User u
        JOIN u.roles r
        WHERE r.roleName = 'ARTIST'
        """)
    List<User> findAllArtists();

    @Query("""
    SELECT new com.ra.base_spring_boot.dto.resp.TrendingArtistResponseDTO(
        u.id,
        CONCAT(u.firstName, ' ', u.lastName),
        u.profileImage,
        u.bio,
        COUNT(DISTINCT sh),
        COUNT(DISTINCT sr),
        COUNT(DISTINCT d)
    )
    FROM User u
    JOIN u.roles r
    LEFT JOIN u.songs s
    LEFT JOIN s.songHistories sh
    LEFT JOIN s.songReactions sr
    LEFT JOIN s.downloads d
    WHERE r.roleName = 'ROLE_ARTIST'
    GROUP BY u.id, u.firstName, u.lastName, u.profileImage, u.bio
    ORDER BY COUNT(DISTINCT sh) DESC
""")
    List<TrendingArtistResponseDTO> findTrendingArtists();

    @Query("SELECT u.status, COUNT(u) FROM User u GROUP BY u.status")
    List<Object[]> countUsersByStatus();

    @Query("""
        SELECT\s
            COALESCE(sp.planName, 'Free') AS accountType,
            COUNT(DISTINCT u.id)
        FROM User u
        LEFT JOIN Subscription s ON s.user = u\s
            AND s.endTime > CURRENT_TIMESTAMP\s
            AND s.startTime <= CURRENT_TIMESTAMP
        LEFT JOIN SubscriptionPlan sp ON s.plan_id = sp
        GROUP BY COALESCE(sp.planName, 'Free')
   \s""")
    List<Object[]> countUsersByAccountType();

    List<User> findByStatusAndAccountExpirationBefore(
            UStatus status, LocalDateTime time);


}
