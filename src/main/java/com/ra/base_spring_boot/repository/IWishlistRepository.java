package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IWishlistRepository extends JpaRepository<User, Long> {
    @Query("SELECT s FROM User u JOIN u.wishlistSongs s WHERE u.id = :userId ORDER BY " +
            "CASE WHEN :sortDir = 'asc' THEN s.views END ASC, " +
            "CASE WHEN :sortDir = 'desc' THEN s.views END DESC")
    Page<Song> findWishlistByUserIdSorted(@Param("userId") Long userId, @Param("sortDir") String sortDir, Pageable pageable);

}

