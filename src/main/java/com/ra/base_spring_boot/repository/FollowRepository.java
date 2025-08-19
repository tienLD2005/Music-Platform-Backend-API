package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Follow;
import com.ra.base_spring_boot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerAndArtist(User follower, User artist);

    List<Follow> findByFollower(User follower);

    List<Follow> findByArtist(User artist);

    long countByArtist(User artist);
}
