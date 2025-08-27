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

    @Query("SELECT s FROM User u JOIN u.wishlistSongs s WHERE u.email = :email ORDER BY s.views ASC")
    Page<Song> findWishlistOrderByViewsAsc(@Param("email") String email, Pageable pageable);

    @Query("SELECT s FROM User u JOIN u.wishlistSongs s WHERE u.email = :email ORDER BY s.views DESC")
    Page<Song> findWishlistOrderByViewsDesc(@Param("email") String email, Pageable pageable);

    //ORDER CREATED
    @Query("SELECT s FROM User u JOIN u.wishlistSongs s WHERE u.email = :email ORDER BY s.createdAt ASC")
    Page<Song> findWishlistOrderByCreatedAtAsc(@Param("email") String email, Pageable pageable);

    @Query("SELECT s FROM User u JOIN u.wishlistSongs s WHERE u.email = :email ORDER BY s.createdAt DESC")
    Page<Song> findWishlistOrderByCreatedAtDesc(@Param("email") String email, Pageable pageable);


}

